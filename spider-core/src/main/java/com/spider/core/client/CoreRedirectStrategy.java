package com.spider.core.client;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * 重定向策略
 * Created by wangpeng on 2017/3/13.
 */
public class CoreRedirectStrategy extends DefaultRedirectStrategy {

    @Override
    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        if(response != null){
            int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode == 301 || responseCode == 302){
                return true;
            }
        }
        return false;
    }
}
