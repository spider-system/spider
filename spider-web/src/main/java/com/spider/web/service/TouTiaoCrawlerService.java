package com.spider.web.service;

import com.alibaba.fastjson.JSON;
import com.spider.common.constants.GlobConts;
import com.spider.common.response.ReturnT;
import com.spider.common.utils.ToutiaoUtil;
import com.spider.core.webmagic.downloader.HttpSwitchProyDownloader;
import com.spider.core.webmagic.monitor.SpiderMonitor;
import com.spider.core.webmagic.monitor.SpiderStatus;
import com.spider.core.webmagic.pipeline.ToutiaoAppPipeline;
import com.spider.core.webmagic.processor.ToutiaoAppPageProcessor;
import com.spider.core.webmagic.proxy.ProxyPool;
import com.spider.core.webmagic.proxy.entity.Proxy;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import javax.management.JMException;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: wangpeng
 * @date: 2018/11/14 23:23
 */
@Service
public class TouTiaoCrawlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TouTiaoCrawlerService.class);

    private static ExecutorService crawlerPoll = Executors.newCachedThreadPool();



    public ReturnT startCrawler(String task){
        //get spider
        SpiderStatus spiderStatus = SpiderMonitor.instance().getSpiderStatusByUUID(task);
        if(spiderStatus != null && spiderStatus.getStatus().equals(String.valueOf(Spider.Status.Running))){
            return new ReturnT().failureData("任务{"+task+"}正在执行中");
        }
        if(spiderStatus != null){
            spiderStatus.start();
            return new ReturnT().successDefault();
        }
        //todo get param from db
        String paramStr = "{\"LBS_status\":\"deny\",\"ab_client\":\"a1,f2,f7,e1\",\"ab_feature\":\"z1\",\"ab_version\":\"574248,580098,570602,587869,486953,548454,577228,586023,571130,591886,239096,568569,170988,493250,571684,374119,588069,581761,576062,569578,562844,550042,435213,320832,586994,569343,545895,405355,578707,521962,584498,522765,416055,592541,558140,555254,378451,471407,579057,593074,582987,574603,271178,587785,585657,326532,591615,586291,573284,594159,583111,589794,593483,591900,587314,469022,554836,549647,424176,583593,31210,572465,583280,590232,591177,442255,593643,589412,584528,590522,569778,582114,546700,280449,281295,589814,473328,581398,325616,578587,590694,586519,511255,568792,498375,580578,467513,593904,252783,566292,444464,584240,579905,580448,590265,589102,586956,590514,572567,457481,562442\",\"ac\":\"WIFI\",\"aid\":\"13\",\"app_name\":\"news_article\",\"channel\":\"App Store\",\"city\":\"\",\"concern_id\":\"6286225228934679042\",\"count\":\"20\",\"detail\":\"1\",\"device_id\":\"59029797261\",\"device_platform\":\"iphone\",\"device_type\":\"iPhone 7 Plus\",\"fp\":\"crT_P2mucWLMFlTZP2U1F2KIFzKe\",\"idfa\":\"C2C21E1A-60E3-4E00-884B-71E72432074F\",\"idfv\":\"D9C014A4-65BE-4ECD-90A6-F162DAE0FB06\",\"iid\":\"50103135774\",\"image\":\"1\",\"language\":\"zh-Hans-CN\",\"last_refresh_sub_entrance_interval\":\"${last_refresh_sub_entrance_interval}\",\"list_count\":\"${list_count}\",\"loc_mode\":\"0\",\"min_behot_time\":\"${min_behot_time}\",\"openudid\":\"c5b86e8e398cee66cdb09acee7ab2d7fd161970a\",\"os_version\":\"12.1\",\"refer\":\"1\",\"refresh_reason\":\"0\",\"resolution\":\"1242*2208\",\"session_refresh_idx\":\"4\",\"ssmix\":\"a\",\"st_time\":\"56\",\"strict\":\"0\",\"tma_jssdk_version\":\"1.3.0.3\",\"ts\":\"1542179121\",\"tt_from\":\"${tt_from}\",\"update_version_code\":\"69722\",\"version_code\":\"6.9.7\",\"vid\":\"D9C014A4-65BE-4ECD-90A6-F162DAE0FB06\"}";
        Map<String,String> paramMap = JSON.parseObject(paramStr,Map.class);
        String rootUrl = "http://is.snssdk.com/api/news/feed/v88/";
        String params = ToutiaoUtil.contactUrlParams(paramMap);
        Long time = System.currentTimeMillis();
        Long min_behot_time = time - 1000*60*60;
        params = params.replace("${min_behot_time}",String.valueOf(min_behot_time))
                .replace("${last_refresh_sub_entrance_interval}",String.valueOf(time))
                .replace("${loc_time}",String.valueOf(time))
                .replace("${tt_from}","enter_auto")
                .replace("${list_count}","1");
        Spider toutiaoSpider =
        Spider.create(new ToutiaoAppPageProcessor())
                .addUrl(rootUrl+"?"+params)
                .addPipeline(new ToutiaoAppPipeline(GlobConts.STORE_DATA_PATH))
                .setDownloader(new HttpSwitchProyDownloader())
                .setUUID(task)
                .thread(100);
        try {
            SpiderMonitor.instance().register(toutiaoSpider);//监控spider
        } catch (JMException e) {
            LOGGER.error(e.getMessage(),e);
        }
        crawlerPoll.execute(toutiaoSpider);
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

    public ReturnT getProxy(){
        Proxy proxy = ProxyPool.proxyQueue.poll();
        return new ReturnT().sucessData(proxy);
    }


    public ReturnT importCrawlerUrl(String url){
        url = URLDecoder.decode(url);
        String[] arrs = url.split("\\?");
        String[] params = arrs[1].split("&");
        Map<String,String> parameters = new HashMap<>();
        for (String param : params) {
            String[] kv = param.split("=");
            String key = kv[0];
            String value = "";
            if(kv.length == 2){
                value = kv[1];
            }
            if("as".equals(key) || "cp".equals(key)){
                continue;
            }
            if("last_refresh_sub_entrance_interval".equals(key) || "min_behot_time".equals(key)
                    ||  "list_count".equals(key) ||  "tt_from".equals(key)){
                value = "${"+key+"}";
            }
            parameters.put(key,value);
        }
        //record parameter
        try {
            FileUtils.writeByteArrayToFile(new File("/Users/wangpeng/Documents/webmagic-crawler-file/url/"+System.currentTimeMillis()+".json"),JSON.toJSONString(parameters).getBytes());
        } catch (IOException e) {
            //
        }
        return new ReturnT().successDefault();
    }

}
