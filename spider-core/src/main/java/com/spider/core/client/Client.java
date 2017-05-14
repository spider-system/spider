package com.spider.core.client;

import com.spider.common.constants.GlobConts;
import com.spider.core.excption.SpiderException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * Created by wangpeng on 2017/3/13.
 */
public class Client {

    public static String USER_AGENT_PC = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    public static String USER_AGENT_MOBILE = "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4";

    private List<Header> headerList = new LinkedList<Header>();
    private List<NameValuePair> pairList = new LinkedList<NameValuePair>();

    private BasicHttpParams params = new BasicHttpParams();
    private static RedirectStrategy redirectStrategy = new CoreRedirectStrategy();
    private static HttpRequestRetryHandler retryHandler = new CoreRetryHandler();

    private String charset = "utf-8";
    private HttpContext httpContext = null;
    private HttpClient httpClient = null;

    private HttpResponse response = null;
    private HttpEntity httpEntity = null;
    private Result result = null;
    private String lastRequestUrl = null;//最后一次请求的地址
    private boolean consumed = false;
    private boolean clientInited = false;

    public Client() {
        initParams();
        enableSSL();
    }
    public static Client newInstance() {
        return new Client();
    }

    /**
     * 复用Client对象，保留HttpParams、Headers，重新使用新的HttpClient
     */
    public Client reset() {
        ClientUtils.shutdown(httpClient);
        this.pairList.clear();
        this.headerList.clear();
        this.httpEntity = null;
        this.httpClient = null;
        this.response = null;
        this.result = null;
        this.consumed = false;
        this.lastRequestUrl = null;
        this.clientInited = false;
        this.enableSSL();
        this.setPCMode();
        return this;
    }

    /**
     * 使用自定义的HttpClient
     */
    public Client httpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    /**
     * 清除HttpParams
     */
    public void clearParams() {
        this.params.clear();
    }
    /**
     * 清除请求的Headers
     */
    public void clearHeaders() {
        this.headerList.clear();
    }
    /**
     * 清除HttpContext
     */
    public void clearHttpContext() {
        this.httpContext = null;
    }


    /**
     * 设置代理
     */
    public Client proxy(String ip, int port) {
        if (StringUtils.isNoneBlank(ip)) {
            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(ip, port));
        }
        else {
            params.removeParameter(ConnRoutePNames.DEFAULT_PROXY);
        }
        return this;
    }


    /**
     * 添加请求的表单项
     */
    public Client form(NameValuePair pair) {
        pairList.add(pair);
        return this;
    }

    /**
     * 不支持GZIP
     * @return
     */
    public Client disableGzip() {
        return header("Accept-Encoding", "deflate, sdch");
    }

    /**
     * 添加请求的表单项
     * @param params
     * @return
     */
    public Client form(Map<String, String> params) {
        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            form(entry.getKey(), entry.getValue()!=null?entry.getValue().toString():"");
        }
        return this;
    }

    /**
     * 添加请求的表单项
     */
    public Client form(String name, String value) {
        pairList.add(new BasicNameValuePair(name, value));
        return this;
    }
    /**
     * 添加请求的表单项列表
     */
    public Client form(List<NameValuePair> pairs) {
        pairList.addAll(pairs);
        return this;
    }
    /**
     * 添加请求的实体
     */
    public Client form(HttpEntity httpEntity) {
        this.httpEntity = httpEntity;
        return this;
    }
    /**
     * 添加请求的表单项数组
     */
    public Client form(NameValuePair[] pairs) {
        for (NameValuePair pair : pairs) {
            pairList.add(pair);
        }
        return this;
    }

    /**
     * 设置referer
     * @param referer
     * @return
     */
    public Client referer(String referer) {
        header(HeaderEnum.Referer.getValue(), referer);
        return this;
    }

    /**
     * 使用移动端模式
     * @return
     */
    public Client setMobileMode() {
        return userAgent(USER_AGENT_MOBILE);
    }



    /**
     * 设置内容的字符集
     */
    public Client charset(String charset) {
        HttpProtocolParams.setContentCharset(params, charset);
        this.charset = charset;
        return this;
    }
    /**
     * 设置上下文
     */
    public Client httpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
        return this;
    }


    private HttpUriRequest buildRequest(HttpUriRequest request) {
        if (headerList.size() > 0) {
            for (Header header : headerList) {
                request.addHeader(header);
            }
        }
        if (request instanceof HttpEntityEnclosingRequestBase) {
            if (pairList.size() > 0) {
                try {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairList, charset);
                    ((HttpEntityEnclosingRequestBase)request).setEntity(entity);
                }
                catch (Exception e) {
                    throw new SpiderException("");
                }
            }
            else if (httpEntity != null) {
                try {
                    ((HttpEntityEnclosingRequestBase)request).setEntity(httpEntity);
                }
                catch (Exception e) {
                    throw new SpiderException("");
                }
            }
        }

        return request;
    }

    private void tryRequest(final HttpClient httpClient, final HttpUriRequest request, final HttpContext httpContext, boolean catchProxyException) {
//        CrawlerProxy crawlerProxy = null;
        try {
//            crawlerProxy = getCrawlerProxy();
//			if (crawlerProxy != null) {
//				logger.info("使用代理用户名"+crawlerProxy.getProxyUsername()+" ip："+crawlerProxy.getHost());
//			}
            response = null;
            response = httpClient.execute(request, httpContext);
            result.statusLine = response.getStatusLine();
            result.headers = response.getAllHeaders();

//            if (crawlerProxy != null) {
//                CrawlerProxyManager.getInstance().removeFailedRedial(crawlerProxy.getProxyUsername());
//            }
        }
        catch (Exception e) {
            if (catchProxyException &&
                    (e instanceof org.apache.http.conn.ConnectTimeoutException || e instanceof org.apache.http.conn.HttpHostConnectException)) {
//                if (e.getMessage() != null && e.getMessage().contains(":"+ FundamentalConfigProvider.getInt(ConfigKeys.PROXY_PORT_KEY, 8888))) {
//                    //重试
//                    logger.error("代理ip使用异常，"+(crawlerProxy!=null?crawlerProxy.getProxyUsername()+" ":"")+e.getMessage());
//                    logger.info("代理请求失败，取消代理再次尝试");
//
//                    retryRequestWithoutProxy(request, httpContext);
//                    return;
//                }
            }
            result.exception = e;
//            throw new CrawlerException(ResultCodeEnum.CRAWLER_REQUEST_ERROR, e);
            throw new SpiderException("");
        }
    }

    private void retryRequestWithoutProxy(HttpUriRequest request, final HttpContext httpContext) {
        //代理ip连接不上
        //清除代理
//        CrawlerProxy proxy = getCrawlerProxy();
//        if (proxy != null && proxy.getProxyUsername() != null) {
//            CrawlerProxyManager.getInstance().addRedial(proxy.getProxyUsername(), proxy.getHost());
//        }
//        //取消代理
//        Client.disableCrawlerProxy();

        //清除代理ip，重新设置CredentialsProvider
        proxy(null, 0);

        //ssl的httpclient重新创建
        enableSSL(true);

        //清除请求上下文中的代理配置
        httpContext.removeAttribute(ClientContext.CREDS_PROVIDER);
        httpContext.removeAttribute(ClientContext.REQUEST_CONFIG);

        //再次请求
        if (request instanceof HttpPost) {
            post(request.getURI().toString(), httpContext, false);
        }
        else {
            get(request.getURI().toString(), httpContext, false);
        }
    }

    private void buildRedirectStrategy(DefaultHttpClient client) {
        if (redirectStrategy == null) {
            return;
        }
        client.setRedirectStrategy(redirectStrategy);
    }

    private void buildProxy(DefaultHttpClient client) {
        client.setCredentialsProvider(buildCredentialsProvider());
    }

    /**
     * GET请求
     */
    public Client get(String url) {
        return get(url, httpContext);
    }
    /**
     * GET请求
     */
    public Client get(String url, HttpContext httpContext) {
        return get(url, httpContext, true);
    }
    /**
     * GET请求
     */
    public Client get(String url, HttpContext httpContext, boolean catchProxyException) {
        return request(url, httpContext, new HttpGet(url), catchProxyException);
    }


    public Client enableSSL() {
        return enableSSL(false);
    }

    public Client enableSSL(boolean forceReset) {
        if (clientInited && !forceReset) {
            return this;
        }
        try {
            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new CoreTrustStrategy()).build();

            HttpClientBuilder builder = HttpClients.custom()
                    .setSSLSocketFactory(new CoreSSLConnectionSocketFactory(sslContext))
                    .setRedirectStrategy(redirectStrategy)
                    .setRetryHandler(retryHandler)
                    .setUserAgent(getUserAgent())
                    ;

            //cookie策略
            String cookiePolicy = (String) params.getParameter(ClientPNames.COOKIE_POLICY);
            if (cookiePolicy != null && cookiePolicy.equals(org.apache.http.client.config.CookieSpecs.BROWSER_COMPATIBILITY)) {
                CookieSpecProvider myCookieSpecProvider = new CoreCookieSpecProvider();
                String specProviderName = "myCookieSpecProvider";
                Registry<CookieSpecProvider> cookieSpecRegistry = RegistryBuilder.<CookieSpecProvider>create()
                        .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                        .register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())
                        .register(specProviderName, myCookieSpecProvider)
                        .build();
                requestConfigBuilder.setCookieSpec(specProviderName);
                builder.setDefaultCookieSpecRegistry(cookieSpecRegistry);
            }

            Boolean circularRedirectsAllowed = (Boolean) params.getParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS);
            if (circularRedirectsAllowed != null) {
                requestConfigBuilder.setCircularRedirectsAllowed(circularRedirectsAllowed);
            }
            Boolean redirectsEnabled = (Boolean) params.getParameter(ClientPNames.HANDLE_REDIRECTS);
            if (redirectsEnabled != null) {
                requestConfigBuilder.setRedirectsEnabled(redirectsEnabled);
            }
            Integer maxRedirects = (Integer) params.getParameter(ClientPNames.MAX_REDIRECTS);
            if (maxRedirects != null) {
                requestConfigBuilder.setMaxRedirects(maxRedirects);
            }
            Integer connectTimeout = (Integer) params.getParameter(CoreConnectionPNames.CONNECTION_TIMEOUT);
            if (connectTimeout != null) {
                requestConfigBuilder.setConnectTimeout(connectTimeout);
                requestConfigBuilder.setConnectionRequestTimeout(connectTimeout);
            }
            Integer socketTimeout = (Integer) params.getParameter(CoreConnectionPNames.SO_TIMEOUT);
            if (socketTimeout != null) {
                requestConfigBuilder.setSocketTimeout(socketTimeout);
            }
            //代理
            HttpHost httpHost = (HttpHost) params.getParameter(ConnRoutePNames.DEFAULT_PROXY);
            if (httpHost != null) {
                requestConfigBuilder.setProxy(httpHost);
            }

           // builder.setDefaultCredentialsProvider(buildCredentialsProvider());//代理设置
            builder.setDefaultRequestConfig(requestConfigBuilder.build());
            this.httpClient = builder.build();
        } catch (Exception e) {

        } finally {
            this.clientInited = true;
        }
        return this;
    }


    /**
     * 代理设置
     * @return
     */
    private CredentialsProvider buildCredentialsProvider() {
       /* CrawlerProxy crawlerProxy = Client.getCrawlerProxy();
        if (crawlerProxy == null) {
            return null;
        }

        CredentialsProvider provider = null;
        if (StrUtils.notEmpty(crawlerProxy.getUsername()) && StrUtils.notEmpty(crawlerProxy.getPassword())) {
            provider = new BasicCredentialsProvider();

            AuthScope authScope = new AuthScope(crawlerProxy.getHost(), crawlerProxy.getPort());
            UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(
                    crawlerProxy.getUsername(), crawlerProxy.getPassword());
            provider.setCredentials(authScope, usernamePasswordCredentials);
        }
        return provider;*/
        return null;
    }



    /**
     * POST请求
     */
    public Client post(String url) {
        return post(url, httpContext);
    }
    /**
     * POST请求
     */
    public Client post(String url, HttpContext httpContext) {
        return post(url, httpContext, true);
    }
    /**
     * POST请求
     */
    public Client post(String url, HttpContext httpContext, boolean catchProxyException) {
        return request(url, httpContext, new HttpPost(url), catchProxyException);
    }

    public Client request(String url, HttpContext httpContext, HttpUriRequest request) {
        return request(url, httpContext, request, true);
    }
    public Client request(String url, HttpContext httpContext, HttpUriRequest request, boolean catchProxyException) {
        this.lastRequestUrl = url;
        this.result = new Result();

        buildRequest(request);

        HttpClient client = httpClient != null ? httpClient : ClientUtils.newHttpClient(params);
        if (client instanceof DefaultHttpClient) {
            buildRedirectStrategy((DefaultHttpClient) client);
            buildProxy((DefaultHttpClient) client);
        }

        tryRequest(client, request, httpContext, catchProxyException);

        return this;
    }

    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    /**
     * 获取请求响应数据
     */
    public String getData() {
        if (result == null || response == null) {
            return null;
        }
        if (result.data != null || consumed) {
            return result.data;
        }
        try {
            result.data = ClientUtils.getContentFromResponseEx(response, charset);
        } catch (Exception e) {
            result.exception = e;
//            throw new CrawlerException(ResultCodeEnum.CRAWLER_SYSTEM_ERROR, e);
            throw new SpiderException("");
        } finally {
            ClientUtils.shutdown(httpClient);
            consumed = true;
        }
        return result.data;
    }

    /**
     * 获取二进制数据
     * @return
     */
    public byte[] getBytes() {
        if (result == null || response == null) {
            return null;
        }
        if (result.bytes != null || consumed) {
            return result.bytes;
        }
        try {
            result.bytes = EntityUtils.toByteArray(response.getEntity());
        } catch (IOException e) {
            result.exception = e;
//            throw new CrawlerException(ResultCodeEnum.CRAWLER_SYSTEM_ERROR, e);
            throw new SpiderException("");
        } finally {
            ClientUtils.shutdown(httpClient);
            consumed = true;
        }
        return result.bytes;
    }

    /**
     * 获取图片二进制数据
     * @return
     */
    public byte[] getImageBytes() {
        Header header = getHeader(HeaderEnum.Content_Type.getValue());
        if (header != null && header.getValue().contains("text")) {
            return null;
        }
        return getBytes();
    }


    /**
     * 关闭HttpClient
     * @return
     */
    public Client shutdown() {
        ClientUtils.shutdown(httpClient);
        return this;
    }

    /**
     * 获取result对象
     * @return
     */
    public Result getResult() {
        return this.result;
    }

    /**
     * 获取请求的表单列表
     * @return
     */
    public List<NameValuePair> getPairList() {
        return this.pairList;
    }

    /**
     * 获取请求的头列表
     * @return
     */
    public List<Header> getHeaderList() {
        return this.headerList;
    }

    /**
     * 获取字符集
     * @return
     */
    public String getCharset() {
        return this.charset;
    }

    /**
     * 获取最后一次请求地址
     * @return
     */
    public String getLastRequestUrl() {
        return this.lastRequestUrl;
    }

    /**
     * 获取请求异常
     */
    public Exception getException() {
        return result == null ? null : result.exception;
    }
    /**
     * 获取请求响应的Header数组
     */
    public Header[] getHeaders() {
        return result == null ? null : result.headers;
    }
    /**
     * 获取请求响应的Header数组
     */
    public int getStatusCode() {
        StatusLine statusLine = getStatusLine();
        if (statusLine != null) {
            return statusLine.getStatusCode();
        }
        return -1;
    }

    /**
     * 从响应头中获取cookie字符串
     */
    public String getCookie() {
        Header[] headers = getHeaders();
        if (headers != null) {
            return ClientUtils.getCookie(headers);
        }
        return null;
    }
    /**
     * 获取指定名称的请求响应的Header
     */
    public Header getHeader(String name) {
        Header[] headers = getHeaders();
        if (headers != null && headers.length > 0) {
            for (Header header : headers) {
                if (name.equalsIgnoreCase(header.getName())) {
                    return header;
                }
            }
        }
        return null;
    }
    /**
     * 获取指定名称的请求响应的最后一个Header
     */
    public Header getLastHeader(String name) {
        Header[] headers = getHeaders();
        if (headers != null && headers.length > 0) {
            int length = headers.length;
            for (int i=length-1;i>=0;i--) {
                if (name.equalsIgnoreCase(headers[i].getName())) {
                    return headers[i];
                }
            }
        }
        return null;
    }
    /**
     * 获取请求响应
     */
    public HttpResponse getResponse() {
        return response;
    }
    public void printResponse() {
        //logger.info(getResponseText());
    }
    public StatusLine getStatusLine() {
        return result == null ? null : result.statusLine;
    }
    public void printStatusLine() {
        StatusLine statusLine = getStatusLine();
        if (statusLine != null) {
            //logger.info(statusLine.toString());
        }
    }
    public void printData() {
        //logger.info(getData());
    }
    public void printHeaders() {
        //logger.info(getHeaderText());
    }


    public String getHeaderText() {
        StringBuilder sb = new StringBuilder();
        Header[] headers = getHeaders();
        if (headers != null && headers.length > 0) {
            for (Header header : headers) {
                sb.append(header).append("\n");
            }
            sb.substring(0, sb.length()-1);
        }
        return sb.toString();
    }

    public String getResponseText() {
        StringBuilder sb = new StringBuilder();
        sb.append(getStatusLine());
        sb.append(getHeaderText());
        sb.append(getData());
        return sb.toString();
    }



    /**
     * 使用默认的参数初始化HttpParams
     */
    public void initParams() {
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        redirecting(true);
        allowCircularRedirects(true);
        maxRedirects(10);
        connectionTimeout(15000);
        socketTimeout(45000);
        cookiePolicy(org.apache.http.client.config.CookieSpecs.BROWSER_COMPATIBILITY);
        setPCMode();


        //代理机制
//        CrawlerProxy crawlerProxy = getCrawlerProxy();
//        if (crawlerProxy != null) {
//            proxy(crawlerProxy.getHost(), crawlerProxy.getPort());
//        }
    }

    /**
     * 设置是否支持自动跳转
     */
    public Client redirecting(boolean value) {
        HttpClientParams.setRedirecting(params, value);
        return this;
    }
    /**
     * 设置是否支持循环跳转
     */
    public Client allowCircularRedirects(boolean value) {
        params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, value);
        return this;
    }
    /**
     * 设置最大支持跳转的数量
     */
    public Client maxRedirects(int max) {
        params.setParameter(ClientPNames.MAX_REDIRECTS, max);
        return this;
    }

    /**
     * 设置连接超时时间
     */
    public Client connectionTimeout(int timeout) {
        HttpConnectionParams.setConnectionTimeout(params, timeout);
        return this;
    }
    /**
     * 设置读取超时时间
     */
    public Client socketTimeout(int timeout) {
        HttpConnectionParams.setSoTimeout(params, timeout);
        return this;
    }

    /**
     * 设置cookie的策略
     */
    public Client cookiePolicy(String policy) {
        params.setParameter(ClientPNames.COOKIE_POLICY, policy);
        return this;
    }

    /**
     * 使用移动端模式
     * @return
     */
    public Client setPCMode() {
        return userAgent(USER_AGENT_PC);
    }

    /**
     * 设置User-Agent
     * @param userAgent
     * @return
     */
    public Client userAgent(String userAgent) {
        header(HeaderEnum.User_Agent.getValue(), userAgent);
        return this;
    }

    /**
     * 添加请求的Header
     */
    public Client header(String name, String value) {
        return header(name, value, false);
    }

    /**
     * 添加请求的Header
     */
    public Client header(String name, String value, boolean override) {
        return header(new BasicHeader(name, value), override);
    }

    /**
     * 添加请求的Header
     */
    public Client header(Header header, boolean override) {
        if (override) {
            for (int i=headerList.size()-1;i>=0;i--) {
                if (headerList.get(i).getName().equalsIgnoreCase(header.getName())) {
                    headerList.remove(i);
                    break;
                }
            }
        }

        headerList.add(header);
        return this;
    }

    //本地线程对象缓存
    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>();
    public static void init() {
        if (threadLocal.get() == null) {
            threadLocal.set(new HashMap<String, Object>());
        }
    }
    public static void destroy() {
        threadLocal.remove();
    }
    public static Map<String, Object> getThreadLocalMap() {
        return threadLocal.get();
    }
    public static String getUserAgent() {
        if (threadLocal.get() != null) {
            return getString(GlobConts.USER_AGENT);
        }
        return null;
    }
    public static String getString(String key) {
        if (threadLocal.get() != null) {
            Object value = getThreadLocalMap().get(key);
            return value != null ? value.toString() : null;
        }
        return null;
    }
    public static Object getObject(String key) {
        if (threadLocal.get() != null) {
            Object value = getThreadLocalMap().get(key);
            return value != null ? value : null;
        }
        return null;
    }
    public static void put(String key, Object value) {
        if (threadLocal.get() != null) {
            getThreadLocalMap().put(key, value);
        }
    }
    public static Object remove(String key) {
        if (threadLocal.get() != null) {
            return getThreadLocalMap().remove(key);
        }
        return null;
    }




    public class Result {
        private String data;//文本数据
        private byte[] bytes;//字节数组
        private Header[] headers;//相应头
        private StatusLine statusLine;//请求状态
        private Exception exception;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public Header[] getHeaders() {
            return headers;
        }

        public void setHeaders(Header[] headers) {
            this.headers = headers;
        }

        public StatusLine getStatusLine() {
            return statusLine;
        }

        public void setStatusLine(StatusLine statusLine) {
            this.statusLine = statusLine;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }
    }
}
