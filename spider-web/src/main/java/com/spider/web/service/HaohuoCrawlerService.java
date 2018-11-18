package com.spider.web.service;

import com.spider.business.repostory.mapper.HaohuoCommodityMapper;
import com.spider.common.bean.HaohuoCommodity;
import com.spider.common.response.ReturnT;
import com.spider.core.webmagic.handler.ToutiaoAdDataHandler;
import com.spider.core.webmagic.monitor.SpiderMonitor;
import com.spider.core.webmagic.monitor.SpiderStatus;
import com.spider.core.webmagic.processor.HaohuoApiProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import javax.management.JMException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Wangchangpeng
 * @date: 2018/11/17.
 */
@Service
public class HaohuoCrawlerService implements ToutiaoAdDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HaohuoCrawlerService.class);

    @Autowired
    private HaohuoCommodityMapper haohuoCommodityMapper;

    private static ExecutorService haohuoCrawlerPoll = Executors.newCachedThreadPool();

    private static String productUrl = "https://haohuo.snssdk.com/product/ajaxstaticitem?id=";

    public ReturnT startCrawler(String task, String productId){
        //get spider
        SpiderStatus spiderStatus = SpiderMonitor.instance().getSpiderStatusByUUID(task);
        if(spiderStatus != null && spiderStatus.getStatus().equals(String.valueOf(Spider.Status.Running))){
            return new ReturnT().failureData("任务{"+task+"}正在执行中");
        }
        if(spiderStatus != null){
            spiderStatus.start();
            return new ReturnT().successDefault();
        }
        Spider haohuoSpider = Spider.create(new HaohuoApiProcessor())
                        .addUrl(productUrl + productId)
//                        .addPipeline(new HaohuoApiPipeline())
                        .setUUID(task)
                        .thread(1);
        try {
            SpiderMonitor.instance().register(haohuoSpider);//监控spider
        } catch (JMException e) {
            LOGGER.error(e.getMessage(),e);
        }
        haohuoCrawlerPoll.execute(haohuoSpider);
        return new ReturnT().successDefault();
    }


    public ReturnT startAllCrawler(String task){
        //get spider
        SpiderStatus spiderStatus = SpiderMonitor.instance().getSpiderStatusByUUID(task);
        if(spiderStatus != null && spiderStatus.getStatus().equals(String.valueOf(Spider.Status.Running))){
            return new ReturnT().failureData("任务{"+task+"}正在执行中");
        }
        if(spiderStatus != null){
            spiderStatus.start();
            return new ReturnT().successDefault();
        }
        List<HaohuoCommodity> commodities = haohuoCommodityMapper.query(new HaohuoCommodity());
        if (CollectionUtils.isEmpty(commodities)) {
            return new ReturnT().successDefault();
        }
        for (HaohuoCommodity haohuoCommodity : commodities) {
            Spider haohuoSpider = Spider.create(new HaohuoApiProcessor())
                    .addUrl(productUrl + haohuoCommodity.getProductId())
//                        .addPipeline(new HaohuoApiPipeline())
                    .setUUID(task)
                    .thread(1);
            try {
                SpiderMonitor.instance().register(haohuoSpider);//监控spider
            } catch (JMException e) {
                LOGGER.error(e.getMessage(),e);
            }
            haohuoCrawlerPoll.execute(haohuoSpider);
        }
        return new ReturnT().successDefault();
    }


    public ReturnT stopCrawlerTask(String task){
        SpiderStatus spiderStatus = SpiderMonitor.instance().getSpiderStatusByUUID(task);
        if(spiderStatus == null){
            return new ReturnT().failureData("任务不存在");
        }
        if(String.valueOf(Spider.Status.Running).equals(spiderStatus.getStatus())){
            spiderStatus.stop();
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

    @Override
    public void sendToHaohuoCrawler(String task, String productId) {
        this.startCrawler(task,productId);
    }
}
