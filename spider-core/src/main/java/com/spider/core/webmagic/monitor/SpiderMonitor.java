package com.spider.core.webmagic.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.monitor.SpiderStatusMXBean;
import us.codecraft.webmagic.utils.UrlUtils;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: wangpeng
 * @date: 2018/11/15 22:33
 */
public class SpiderMonitor {

    private static SpiderMonitor INSTANCE = new SpiderMonitor();

    private AtomicBoolean started = new AtomicBoolean(false);

    private Logger logger = LoggerFactory.getLogger(getClass());

    private MBeanServer mbeanServer;

    private String jmxServerName;

    private List<SpiderStatusMXBean> spiderStatuses = new ArrayList<SpiderStatusMXBean>();

    protected SpiderMonitor() {
        jmxServerName = "WebMagic";
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    /**
     * Register spider for monitor.
     *
     * @param spiders spiders
     * @return this
     * @throws JMException JMException
     */
    public synchronized SpiderMonitor register(Spider... spiders) throws JMException {
        for (Spider spider : spiders) {
            SpiderMonitor.MonitorSpiderListener monitorSpiderListener = new SpiderMonitor.MonitorSpiderListener();
            if (spider.getSpiderListeners() == null) {
                List<SpiderListener> spiderListeners = new ArrayList<SpiderListener>();
                spiderListeners.add(monitorSpiderListener);
                spider.setSpiderListeners(spiderListeners);
            } else {
                spider.getSpiderListeners().add(monitorSpiderListener);
            }
            SpiderStatusMXBean spiderStatusMBean = getSpiderStatusMBean(spider, monitorSpiderListener);
            registerMBean(spiderStatusMBean);
            spiderStatuses.add(spiderStatusMBean);
        }
        return this;
    }

    protected SpiderStatusMXBean getSpiderStatusMBean(Spider spider, MonitorSpiderListener monitorSpiderListener) {
        return new SpiderStatus(spider, monitorSpiderListener);
    }

    public SpiderStatus getSpiderStatusByUUID(String task){
        if(spiderStatuses == null || spiderStatuses.size() == 0){
            return null;
        }
        for(SpiderStatusMXBean spiderStatusMXBean : spiderStatuses){
            if(task.equals(spiderStatusMXBean.getName())){
                return (SpiderStatus)spiderStatusMXBean;
            }
        }
        return null;
    }

    public synchronized Boolean unRegisterSpdierByTask(String task){
        if(spiderStatuses == null || spiderStatuses.size() == 0){
            return false;
        }
        Iterator<SpiderStatusMXBean> it = spiderStatuses.iterator();
        while (it.hasNext()){
            SpiderStatusMXBean spiderStatusMXBean = it.next();
            if(task.equals(spiderStatusMXBean.getName())){
                try {
                    unRegisterMBean(spiderStatusMXBean);
                } catch (JMException e) {
                    // exception
                }
                it.remove();
                return true;
            }
        }
        return false;
    }

    public List<SpiderStatusMXBean> getSpiderStatuses(){
        return spiderStatuses;
    }

    public static SpiderMonitor instance() {
        return INSTANCE;
    }

    public class MonitorSpiderListener implements SpiderListener {

        private final AtomicInteger successCount = new AtomicInteger(0);

        private final AtomicInteger errorCount = new AtomicInteger(0);

        private List<String> errorUrls = Collections.synchronizedList(new ArrayList<String>());

        @Override
        public void onSuccess(Request request) {
            successCount.incrementAndGet();
        }

        @Override
        public void onError(Request request) {
            errorUrls.add(request.getUrl());
            errorCount.incrementAndGet();
        }

        public AtomicInteger getSuccessCount() {
            return successCount;
        }

        public AtomicInteger getErrorCount() {
            return errorCount;
        }

        public List<String> getErrorUrls() {
            return errorUrls;
        }
    }

    protected void registerMBean(SpiderStatusMXBean spiderStatus) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        ObjectName objName = new ObjectName(jmxServerName + ":name=" + UrlUtils.removePort(spiderStatus.getName()));
        mbeanServer.registerMBean(spiderStatus, objName);
    }

    protected void unRegisterMBean(SpiderStatusMXBean spiderStatus) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, InstanceNotFoundException {
        ObjectName objName = new ObjectName(jmxServerName + ":name=" + UrlUtils.removePort(spiderStatus.getName()));
        mbeanServer.unregisterMBean(objName);
    }
}
