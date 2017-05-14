package com.spider.core.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;

/**
 * 重试处理机制
 * Created by wangpeng on 2017/3/13.
 */
public class CoreRetryHandler implements HttpRequestRetryHandler {
    private static final int DEFAULT_MAX_RETRY_COUNT = 3;
    private int maxRetryCount = DEFAULT_MAX_RETRY_COUNT;
    public CoreRetryHandler(int max) {
        this.maxRetryCount = max;
    }
    public CoreRetryHandler() {
    }

    @Override
    public boolean retryRequest(IOException exception,
                                int executionCount, HttpContext context) {
        if (executionCount >= maxRetryCount) {// 如果已经重试了N次，就放弃
            return false;
        }
        if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
            return true;
        }
        if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
            return false;
        }
        if (exception instanceof InterruptedIOException) {// 超时
            return false;
        }
        if (exception instanceof UnknownHostException) {// 目标服务器不可达
            return false;
        }
        if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
            return false;
        }
        if (exception instanceof SSLException) {// SSL握手异常
            return false;
        }

        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        // 如果请求是幂等的，就再次尝试
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            return true;
        }
        return false;
    }
}
