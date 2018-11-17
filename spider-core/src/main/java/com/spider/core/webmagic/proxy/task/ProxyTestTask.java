package com.spider.core.webmagic.proxy.task;

import com.spider.common.utils.PropertieUtils;
import com.spider.core.webmagic.proxy.ProxyHttpClient;
import com.spider.core.webmagic.proxy.ProxyPool;
import com.spider.core.webmagic.proxy.entity.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.utils.UrlUtils;

import java.io.IOException;

/**
 * 代理检测task
 * 通过访问知乎首页，能否正确响应
 * 将可用代理添加到DelayQueue延时队列中
 */
public class ProxyTestTask implements Runnable{
    private final static Logger logger = LoggerFactory.getLogger(ProxyTestTask.class);
    private Proxy proxy;
    public ProxyTestTask(Proxy proxy){
        this.proxy = proxy;
    }
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        String url = PropertieUtils.getString("proxy.index.url","https://www.toutiao.com/");
        Request request = new Request(url);
        try {

            Page page = ProxyHttpClient.getInstance().getWebPage(request, Site.me().setDomain(UrlUtils.getDomain(url)).toTask());
            long endTime = System.currentTimeMillis();
            String logStr = Thread.currentThread().getName() + " " + proxy.getProxyStr() +
                    "  executing request " + page.getUrl()  + " response statusCode:" + page.getStatusCode() +
                    "  request cost time:" + (endTime - startTime) + "ms";
            if (page == null || page.getStatusCode() != 200){
                logger.warn(logStr);
                return;
            }
            logger.debug(proxy.toString() + "---------" + page.toString());
            logger.debug(proxy.toString() + "----------代理可用--------请求耗时:" + (endTime - startTime) + "ms");
            ProxyPool.proxyQueue.add(proxy);
        } catch (Exception e) {
            logger.debug("Exception:", e);
        } finally {
        }
    }
    private String getProxyStr(){
        return proxy.getIp() + ":" + proxy.getPort();
    }
}
