package com.spider.core.webmagic.downloader;

import com.spider.common.utils.PropertieUtils;
import com.spider.core.webmagic.proxy.ProxyPool;
import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.util.concurrent.TimeUnit;

/**
 * @author: wangpeng
 * @date: 2018/11/17 00:18
 */
public class HttpSwitchProxyDownloader extends HttpClientDownloader {

    @Override
    public Page download(Request request, Task task) {
        Boolean isUserProxy = false;
        if(PropertieUtils.getBoolean("isProxy",true)){
            Proxy proxy = switchProxy();
            if(proxy != null && StringUtils.isNotEmpty(proxy.getHost()) && proxy.getPort() != 0){
                isUserProxy = true;
                setProxyProvider(SimpleProxyProvider.from(proxy));
            }
        }
        try {
            Page page = super.download(request, task);
            if(isUserProxy && !page.isDownloadSuccess()){
                //do not use proxy to crawler again
                setProxyProvider(null);
                return super.download(request,task);
            }
            return page;
        }catch (Exception e){
            //do not use proxy to crawler again
            setProxyProvider(null);
            return super.download(request,task);
        }
    }


    private Proxy switchProxy(){
        com.spider.core.webmagic.proxy.entity.Proxy proxy = null;
        try {
            proxy = ProxyPool.proxyQueue.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //
        }
        if(proxy != null){
            return new Proxy(proxy.getIp(),proxy.getPort());
        }
        return null;
    }
}
