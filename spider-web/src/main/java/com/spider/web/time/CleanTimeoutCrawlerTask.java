package com.spider.web.time;

import com.spider.common.constants.GlobConts;
import com.spider.core.webmagic.monitor.SpiderMonitor;
import com.spider.core.webmagic.monitor.SpiderStatus;
import com.spider.web.service.TouTiaoCrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderStatusMXBean;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 移除超长时间的爬取任务，最大爬取时间可配置
 * @author: wangpeng
 * @date: 2018/11/15 23:24
 */
@Component
public class CleanTimeoutCrawlerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanTimeoutCrawlerTask.class);


    private static final Lock lock = new ReentrantLock();


    @Scheduled(cron="0/10 * *  * * ? ")//每60s执行一次
    public void cleanUp(){
        lock.lock();
        try {
            List<SpiderStatusMXBean> spiderStatusMXBeans = SpiderMonitor.instance().getSpiderStatuses();
            if(spiderStatusMXBeans == null && spiderStatusMXBeans.size() == 0){
                return;
            }
            for(SpiderStatusMXBean spiderStatusMXBean : spiderStatusMXBeans){
                SpiderStatus spiderStatus = (SpiderStatus)spiderStatusMXBean;
                if(!String.valueOf(Spider.Status.Running).equals(spiderStatus.getStatus())){
                    return;
                }
                Long startTime = spiderStatus.getStartTime().getTime();
                Long runingTime = System.currentTimeMillis()-startTime;
                if(runingTime >= GlobConts.MAX_CRAWLER_TIME){
                    //停止该任务
                    LOGGER.info("remove running too long time task:{}",spiderStatus.getName());
                    spiderStatus.stop();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
