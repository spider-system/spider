package com.spider.core.webmagic.proxy;

import com.spider.core.util.SimpleThreadPoolExecutor;
import com.spider.core.util.ThreadPoolMonitor;
import com.spider.core.webmagic.downloader.HttpSwitchProxyDownloader;
import com.spider.core.webmagic.proxy.task.ProxyPageTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProxyHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(ProxyHttpClient.class);
    private volatile static ProxyHttpClient instance;

    private HttpSwitchProxyDownloader httpDownLoader;


    public static ProxyHttpClient getInstance(){
        if (instance == null){
            synchronized (ProxyHttpClient.class){
                if (instance == null){
                    instance = new ProxyHttpClient();
                }
            }
        }
        return instance;
    }
    /**
     * 代理测试线程池
     */
    private ThreadPoolExecutor proxyTestThreadExecutor;
    /**
     * 代理网站下载线程池
     */
    private ThreadPoolExecutor proxyDownloadThreadExecutor;
    public ProxyHttpClient(){
        initThreadPool();
        initProxy();
        httpDownLoader = new HttpSwitchProxyDownloader();
    }
    /**
     * 初始化线程池
     */
    private void initThreadPool(){
        proxyTestThreadExecutor = new SimpleThreadPoolExecutor(100, 100,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(10000),
                new ThreadPoolExecutor.DiscardPolicy(),
                "proxyTestThreadExecutor");
        proxyDownloadThreadExecutor = new SimpleThreadPoolExecutor(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), "" +
                "proxyDownloadThreadExecutor");
        new Thread(new ThreadPoolMonitor(proxyTestThreadExecutor, "ProxyTestThreadPool")).start();
        new Thread(new ThreadPoolMonitor(proxyDownloadThreadExecutor, "ProxyDownloadThreadExecutor")).start();
    }

    /**
     * 初始化proxy
     *
     */
    private void initProxy(){
    }
    /**
     * 抓取代理
     */
    public void startCrawl(){
        for (String url : ProxyPool.proxyMap.keySet()){
            /**
             * 首次本机直接下载代理页面
             */
            proxyDownloadThreadExecutor.execute(new ProxyPageTask(url, false));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public Page getWebPage(Request request, Task task){
        return httpDownLoader.download(request,task);

    }
    public ThreadPoolExecutor getProxyTestThreadExecutor() {
        return proxyTestThreadExecutor;
    }

    public ThreadPoolExecutor getProxyDownloadThreadExecutor() {
        return proxyDownloadThreadExecutor;
    }
}