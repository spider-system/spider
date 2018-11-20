package com.spider.web.time;

import com.spider.common.constants.GlobConts;
import com.spider.common.utils.PropertieUtils;
import com.spider.core.webmagic.monitor.SpiderMonitor;
import com.spider.core.webmagic.monitor.SpiderStatus;
import com.spider.core.webmagic.proxy.ProxyHttpClient;
import com.spider.core.webmagic.proxy.ProxyPool;
import com.spider.core.webmagic.proxy.entity.Proxy;
import com.spider.core.webmagic.proxy.util.ProxyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderStatusMXBean;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 移除超长时间的爬取任务，最大爬取时间可配置
 * @author: wangpeng
 * @date: 2018/11/15 23:24
 */
@Component
public class CrawlerTaskTimer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerTaskTimer.class);


    private static final Lock lock = new ReentrantLock();

    //private static final Striped<Lock> striped = Striped.lazyWeakLock(127);

    //private static final String CRWALER_TASK_REMOVAL_KEY = "crawler:task";


    @Scheduled(cron="0/10 * *  * * ? ")//每10s执行一次
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


    @Scheduled(fixedDelay = 1000*60*60)
    public void startProxy(){
        //只有使用代理才去代理网站爬取代理
        if(PropertieUtils.getBoolean("isProxy")){
            ProxyHttpClient.getInstance().startCrawl();
        }
    }

    @Scheduled(cron="0/30 * *  * * ? ")//每30s执行一次
    public void removeUnAvalibleProxy(){
        ProxyPool.lock.readLock().lock();
        try {
            Iterator<Proxy> it = ProxyPool.proxySet.iterator();
            while (it.hasNext()){
                Proxy p = it.next();
                if (ProxyUtil.isDiscardProxy(p) || ProxyUtil.isAliveTooLong(p)){
                    if(ProxyPool.proxyQueue.contains(p)){
                        ProxyPool.proxyQueue.remove(p);
                    }
                    it.remove();
                }
            }
        } finally {
            ProxyPool.lock.readLock().unlock();
        }
    }
}
