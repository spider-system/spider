package com.spider.web.service;

import com.alibaba.fastjson.JSON;
import com.spider.business.repostory.mapper.TaskMapper;
import com.spider.common.bean.Task;
import com.spider.common.constants.GlobConts;
import com.spider.common.response.ReturnT;
import com.spider.core.util.SimpleThreadPoolExecutor;
import com.spider.core.util.ToutiaoUtil;
import com.spider.core.webmagic.ToutiaoSpider;
import com.spider.core.webmagic.downloader.HttpSwitchProxyDownloader;
import com.spider.core.webmagic.monitor.SpiderMonitor;
import com.spider.core.webmagic.monitor.SpiderStatus;
import com.spider.core.webmagic.pipeline.ToutiaoAppPipeline;
import com.spider.core.webmagic.processor.ToutiaoAppPageProcessor;
import com.spider.core.webmagic.proxy.ProxyPool;
import com.spider.core.webmagic.proxy.entity.Proxy;
import com.spider.web.listener.ToutiaoCrawlerStatusLisnter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import javax.management.JMException;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author: wangpeng
 * @date: 2018/11/14 23:23
 */
@Service
public class TouTiaoCrawlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TouTiaoCrawlerService.class);

    @Autowired
    private ToutiaoCrawlerStatusLisnter crawlerStatusLisnter;

    @Autowired
    private HaohuoCrawlerService haohuoDataHanlder;

    @Autowired
    private TaskMapper taskMapper;

    private static ExecutorService crawlerPoll = new SimpleThreadPoolExecutor(20, 20,
            6L, TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>(),
            "ToutiaoSpiderThreadExecutor");

    public ReturnT startCrawler(String taskId,String rootUrl,Map<String,String> paramMap){
        //get spider
        SpiderStatus spiderStatus = SpiderMonitor.instance().getSpiderStatusByUUID(taskId);
        if(spiderStatus != null && spiderStatus.getStatus().equals(String.valueOf(Spider.Status.Running))){
            return new ReturnT().failureData("任务{"+taskId+"}正在执行中");
        }
        if(spiderStatus != null){
            spiderStatus.start();
            return new ReturnT().successDefault();
        }
        if(paramMap == null){
            Task task = taskMapper.queryByTaskId(taskId);
            if(task == null){
                return new ReturnT().failureData("task["+taskId+"]不存在");

            }
            if(StringUtils.isBlank(task.getRootUrl()) || StringUtils.isBlank(task.getParams())){
                return new ReturnT().failureData("task["+taskId+"]配置信息有误");
            }
            try {
                paramMap = JSON.parseObject(task.getParams(),Map.class);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
                return new ReturnT().failureData("taskId["+taskId+"]请求信息有误");
            }
            rootUrl = task.getRootUrl();
        }

        if(StringUtils.isEmpty(rootUrl)){
            return new ReturnT().failureData("task["+taskId+"]任务启动失败，rootUrl为空");
        }
        String params = ToutiaoUtil.contactUrlParams(paramMap);
        Long time = System.currentTimeMillis();
        Long min_behot_time = time - GlobConts.CRAWLER_TIME_REGION;
        params = params.replace("${"+GlobConts.MIN_BEHOT_TIME+"}",String.valueOf(min_behot_time))
                .replace("${"+GlobConts.LAST_FRESH_SUB_ENTRANCE_INTERVAL+"}",String.valueOf(time))
                .replace("${"+GlobConts.LOC_TIME+"}",String.valueOf(time))
                .replace("${"+GlobConts.TT_FROM+"}",GlobConts.FIRST_FF_FROM_IOS)
                .replace("${"+GlobConts.LIST_COUNT+"}","1");
        ToutiaoSpider toutiaoSpider = ToutiaoSpider.create(new ToutiaoAppPageProcessor());
        toutiaoSpider.setStatusListener(crawlerStatusLisnter)
                .addUrl(rootUrl+"?"+params)
                .addPipeline(new ToutiaoAppPipeline(GlobConts.STORE_DATA_PATH,haohuoDataHanlder))
                .setDownloader(new HttpSwitchProxyDownloader())
                .setUUID(taskId)
                .thread(100);
        try {
            //监控spider
            SpiderMonitor.instance().register(toutiaoSpider);
        } catch (JMException e) {
            LOGGER.error(e.getMessage(),e);
        }
        crawlerPoll.execute(toutiaoSpider);
        return new ReturnT().successDefault();
    }



    public ReturnT startCrawler(String task){
        return startCrawler(task,null,null);
    }


    public ReturnT stopCrawlerTask(String task){
        SpiderStatus spiderStatus = SpiderMonitor.instance().getSpiderStatusByUUID(task);
        if(spiderStatus == null){
            return new ReturnT().failureData("任务不存在");
        }
        if(String.valueOf(Spider.Status.Running).equals(spiderStatus.getStatus())){
            spiderStatus.stop();
            spiderStatus.setManualStop(true);
            try {
                crawlerStatusLisnter.reportSoptEvent(task,spiderStatus.getStartTime());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            }
            return new ReturnT().successDefault();
        }
        return new ReturnT().failureData("任务不在执行中");
    }

    public ReturnT getTaskStatsByTask(String task){
        SpiderStatus spiderStatus = SpiderMonitor.instance().getSpiderStatusByUUID(task);
        if(spiderStatus == null){
            return new ReturnT().failureData("任务不存在");
        }
        Map map = new HashMap();
        map.put("status",spiderStatus.getStatus());
        map.put("leftPageCount",spiderStatus.getLeftPageCount());
        map.put("errorPageCount",spiderStatus.getErrorPageCount());
        map.put("totalPageCount",spiderStatus.getTotalPageCount());
        map.put("successPageCount",spiderStatus.getSuccessPageCount());
        map.put("startTime",spiderStatus.getStartTime());
        return new ReturnT().sucessData(map);

    }

    public ReturnT getProxy(){
        Proxy proxy = ProxyPool.proxyQueue.poll();
        return new ReturnT().sucessData(proxy);
    }


    public ReturnT importCrawlerUrl(String url){
        Map<String,String> parameters = ToutiaoUtil.parseUrl(url);
        parameters.remove(GlobConts.ROOT_URL_PREFIX);
        //record parameter
        try {
            FileUtils.writeByteArrayToFile(new File("/Users/wangpeng/Documents/webmagic-crawler-file/url/"+System.currentTimeMillis()+".json"),JSON.toJSONString(parameters).getBytes());
        } catch (IOException e) {
            //
        }
        return new ReturnT().successDefault();
    }

}
