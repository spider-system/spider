package com.spider.web.time;

import com.spider.core.webmagic.proxy.ProxyHttpClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author: wangpeng
 * @date: 2018/11/16 23:39
 */
@Component
public class ProxyPoolTask {


    //网页获取免费代理，不太好用，不是超时就是请求时间太长，不建议使用
    @PostConstruct
    public void startGetProxy(){
        ProxyHttpClient.getInstance().startCrawl();
    }
}
