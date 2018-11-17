package com.spider.core.webmagic.downloader;

import com.spider.common.constants.GlobConts;
import com.spider.common.utils.PropertieUtils;
import com.spider.core.webmagic.proxy.ProxyPool;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.HttpClientGenerator;
import us.codecraft.webmagic.downloader.HttpClientRequestContext;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;
import us.codecraft.webmagic.proxy.Proxy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangpeng
 * @date: 2018/11/17 00:18
 */
public class HttpSwitchProxyDownloader extends HttpClientDownloader {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private HttpUriRequestConverter httpSwitchUriRequestConverter = new HttpUriRequestConverter();

    private HttpClientGenerator httpSwitchClientGenerator = new HttpClientGenerator();

    private final Map<String, CloseableHttpClient> httpSwitchClients = new HashMap<String, CloseableHttpClient>();


    @Override
    public Page download(Request request, Task task) {
        Boolean isUserProxy = false;
        Proxy proxy = null;
        if(PropertieUtils.getBoolean("isProxy",true)){
            proxy = switchProxy();
            if(proxy != null && StringUtils.isNotEmpty(proxy.getHost()) && proxy.getPort() != 0){
                isUserProxy = true;
            }
        }
        try {
            Page page = download(request, task,isUserProxy ? proxy : null);
            if(isUserProxy && !page.isDownloadSuccess()){
                //do not use proxy to crawler again
                return download(request, task,null);
            }
            //头条
            if(request.getUrl().contains(GlobConts.TOUTIAO_URL_PREFIX)){
                return checkStatusCodeIfNeedRetry(request,task,page);
            }
            return page;
        }catch (Exception e){
            return download(request, task,null);
        }
    }


    /**
     * 判断是否要重试for toutiao crawler
     * @param request
     * @param task
     * @param page
     * @return
     */
    private Page checkStatusCodeIfNeedRetry(Request request, Task task,Page page){
        int statusCode = page.getStatusCode();
        if(statusCode != HttpStatus.SC_OK){
            //重试一次
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //
            }
            return download(request,task,null);
        }
        return page;
    }

    private Page download(Request request, Task task,Proxy proxy){
        if (task == null || task.getSite() == null) {
            throw new NullPointerException("task or site can not be null");
        }
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = getHttpClient(task.getSite());
        HttpClientRequestContext requestContext = httpSwitchUriRequestConverter.convert(request, task.getSite(), proxy);
        Page page = Page.fail();
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());
            page = handleResponse(request, request.getCharset() != null ? request.getCharset() : task.getSite().getCharset(), httpResponse, task);
            onSuccess(request);
            if(request.getUrl().contains(GlobConts.TOUTIAO_URL_PREFIX)){
                logger.info("downloading page success {}", request.getUrl());
            }
            return page;
        } catch (IOException e) {
            logger.warn("download page {} error", request.getUrl());
            onError(request);
            return page;
        } finally {
            if (httpResponse != null) {
                //ensure the connection is released back to pool
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
        }
    }


    private CloseableHttpClient getHttpClient(Site site) {
        if (site == null) {
            return httpSwitchClientGenerator.getClient(null);
        }
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpSwitchClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpSwitchClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpSwitchClientGenerator.getClient(site);
                    httpSwitchClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }


    private Proxy switchProxy(){
        com.spider.core.webmagic.proxy.entity.Proxy proxy = ProxyPool.proxyQueue.poll();
        if(proxy != null){
            return new Proxy(proxy.getIp(),proxy.getPort());
        }
        return null;
    }
}
