package com.spider.core.webmagic.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderStatusMXBean;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

import java.util.Date;
import java.util.List;

/**
 * @author: wangpeng
 * @date: 2018/11/15 22:37
 */
public class SpiderStatus implements SpiderStatusMXBean {

    protected final Spider spider;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected final SpiderMonitor.MonitorSpiderListener monitorSpiderListener;

    /**
     * 是否手动停止
     */
    private Boolean isManualStop = Boolean.FALSE;

    public SpiderStatus(Spider spider, SpiderMonitor.MonitorSpiderListener monitorSpiderListener) {
        this.spider = spider;
        this.monitorSpiderListener = monitorSpiderListener;
    }

    @Override
    public String getName() {
        return spider.getUUID();
    }

    @Override
    public int getLeftPageCount() {
        if (spider.getScheduler() instanceof MonitorableScheduler) {
            return ((MonitorableScheduler) spider.getScheduler()).getLeftRequestsCount(spider);
        }
        logger.warn("Get leftPageCount fail, try to use a Scheduler implement MonitorableScheduler for monitor count!");
        return -1;
    }

    @Override
    public int getTotalPageCount() {
        if (spider.getScheduler() instanceof MonitorableScheduler) {
            return ((MonitorableScheduler) spider.getScheduler()).getTotalRequestsCount(spider);
        }
        logger.warn("Get totalPageCount fail, try to use a Scheduler implement MonitorableScheduler for monitor count!");
        return -1;
    }

    @Override
    public int getSuccessPageCount() {
        return monitorSpiderListener.getSuccessCount().get();
    }

    @Override
    public int getErrorPageCount() {
        return monitorSpiderListener.getErrorCount().get();
    }

    @Override
    public List<String> getErrorPages() {
        return monitorSpiderListener.getErrorUrls();
    }

    @Override
    public String getStatus() {
        return spider.getStatus().name();
    }

    @Override
    public int getThread() {
        return spider.getThreadAlive();
    }

    @Override
    public void start() {
        spider.start();
    }

    @Override
    public void stop() {
        spider.stop();
    }

    @Override
    public Date getStartTime() {
        return spider.getStartTime();
    }

    @Override
    public int getPagePerSecond() {
        int runSeconds = (int) (System.currentTimeMillis() - getStartTime().getTime()) / 1000;
        return getSuccessPageCount() / runSeconds;
    }

    public Boolean getManualStop() {
        return isManualStop;
    }

    public void setManualStop(Boolean manualStop) {
        isManualStop = manualStop;
    }
}
