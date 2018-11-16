package com.spider.core.webmagic.proxy.task;

import com.spider.common.constants.GlobConts;
import com.spider.common.utils.PropertieUtils;
import com.spider.core.webmagic.proxy.ProxyHttpClient;
import com.spider.core.webmagic.proxy.ProxyPool;
import com.spider.core.webmagic.proxy.entity.Page;
import com.spider.core.webmagic.proxy.entity.Proxy;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

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
        HttpGet request = new HttpGet(PropertieUtils.getString("proxy.index.url","https://www.toutiao.com/"));
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(GlobConts.TIMEOUT).
                    setConnectTimeout(GlobConts.TIMEOUT).
                    setConnectionRequestTimeout(GlobConts.TIMEOUT).
                    setProxy(new HttpHost(proxy.getIp(), proxy.getPort())).
                    setCookieSpec(CookieSpecs.STANDARD).
                    build();
            request.setConfig(requestConfig);
            Page page = ProxyHttpClient.getInstance().getWebPage(request);
            long endTime = System.currentTimeMillis();
            String logStr = Thread.currentThread().getName() + " " + proxy.getProxyStr() +
                    "  executing request " + page.getUrl()  + " response statusCode:" + page.getStatusCode() +
                    "  request cost time:" + (endTime - startTime) + "ms";
            if (page == null || page.getStatusCode() != 200){
                logger.warn(logStr);
                return;
            }
            request.releaseConnection();
            logger.debug(proxy.toString() + "---------" + page.toString());
            logger.debug(proxy.toString() + "----------代理可用--------请求耗时:" + (endTime - startTime) + "ms");
            ProxyPool.proxyQueue.add(proxy);
        } catch (IOException e) {
            logger.debug("IOException:", e);
        } finally {
            if (request != null){
                request.releaseConnection();
            }
        }
    }
    private String getProxyStr(){
        return proxy.getIp() + ":" + proxy.getPort();
    }
}
