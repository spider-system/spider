package com.spider.core.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientUtils.class);


	public static final String COOKIE_DIVIDER = "; ";
	public static final String RESPONSE_COOKIE_NAME = "Set-Cookie";
	public static final String REQUEST_COOKIE_NAME = "Cookie";

	public static void printHttpResponse(HttpResponse response, String charset) {
		try {
			printStatusLine(response);
			printHeaders(response);
			printEntity(response, charset);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printCookieStore(CookieStore cookieStore) {
		if (cookieStore != null) {
			List<Cookie> cookieList = cookieStore.getCookies();
			if (cookieList != null) {
				for (Cookie cookie : cookieList) {
					LOGGER.info(cookie.getName()+":"+cookie.getValue());
				}
			}
		}
	}

	public static void printHeaders(HttpResponse response) {
		try {
			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				LOGGER.info(header.getName()+":"+header.getValue());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printStatusLine(HttpResponse response) {
		try {
			LOGGER.info("response code:{}",response.getStatusLine().getStatusCode());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printEntity(HttpResponse response, String charset) {
		LOGGER.info(getContentFromResponse(response, charset));
	}

	public static HttpEntity decodeGZipEntity(HttpEntity entity) {
		return new InflatingEntity(entity);
	}

	public static class InflatingEntity extends HttpEntityWrapper {
		public InflatingEntity(HttpEntity wrapped) {
			super(wrapped);
		}
		@Override
		public InputStream getContent() throws IOException {
			return new GZIPInputStream(super.wrappedEntity.getContent());
		}
		@Override
		public long getContentLength() {
			return -1;
		}
	}

	/**
	 * 判断HttpResponse返回的HttpEntity是否为gzip类型
	 */
	public static boolean isGZIPEntity(HttpResponse response) {
		Header[] headers = response.getAllHeaders();
		if (headers != null && headers.length > 0) {
			String headerName = null;
			for (Header header : headers) {
				headerName = header.getName();
				if (headerName != null && "Content-Encoding".equalsIgnoreCase(headerName)) {
					return "gzip".equalsIgnoreCase(header.getValue());
				}
			}
		}
		return false;
	}

	public static StringEntity getStringEntity(String data) {
		return getStringEntity(data, null);
	}

	public static StringEntity getStringEntity(String data, String charset) {
		StringEntity entity = null;
		if (data != null) {
			try {
				if (charset == null) {
					charset = "UTF-8";
				}
				entity = new StringEntity(data, charset);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return entity;
	}

	public static String getContentFromResponse(HttpResponse response) {
		return getContentFromResponse(response, null);
	}

	public static String getContentFromResponse(HttpResponse response, String charset) {
		String data = null;
		try {
			return getContentFromResponseEx(response, charset);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
		return data;
	}

	public static String getContentFromResponseEx(HttpResponse response) throws Exception {
		return getContentFromResponseEx(response, null);
	}
	public static String getContentFromResponseEx(HttpResponse response, String charset) throws Exception {
		String data = null;
		HttpEntity entity = response.getEntity();
		if (isGZIPEntity(response)) {
			entity = decodeGZIPEntity(entity);
		}

		if (charset != null) {
			data = toString(entity, charset);
		}
		else {
			data = toString(entity, "UTF-8");
		}
		return data;
	}
	public static String toString(HttpEntity entity) {
		return toString(entity, (String)null);
	}

	public static String toString(HttpEntity entity, String charset) {
		try {
			if (charset == null) {
				charset = "UTF-8";
			}
			return toString(entity, charset != null ? Charset.forName(charset) : null);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
		return null;
	}

	public static String toString(
			final HttpEntity entity, final Charset defaultCharset) throws IOException, ParseException {
		Args.notNull(entity, "Entity");
		final InputStream instream = entity.getContent();
		if (instream == null) {
			return null;
		}
		try {
			Args.check(entity.getContentLength() <= Integer.MAX_VALUE,
					"HTTP entity too large to be buffered in memory");
			int i = (int)entity.getContentLength();
			if (i < 0) {
				i = 4096;
			}
			Charset charset = null;
			try {
				final ContentType contentType = ContentType.get(entity);
				if (contentType != null) {
					charset = contentType.getCharset();
				}
			} catch (final UnsupportedCharsetException ex) {
				if (defaultCharset == null) {
					throw new UnsupportedEncodingException(ex.getMessage());
				}
			}
			if (charset == null) {
				charset = defaultCharset;
			}
			if (charset == null) {
				charset = HTTP.DEF_CONTENT_CHARSET;
			}
			final Reader reader = new InputStreamReader(instream, charset);
			final CharArrayBuffer buffer = new CharArrayBuffer(i);
			final char[] tmp = new char[1024];
			int l;
			try {
				while ((l = reader.read(tmp)) != -1) {
					buffer.append(tmp, 0, l);
				}
			}
			catch (EOFException e) {
			}
			return buffer.toString();
		} finally {
			instream.close();
		}
	}


	public static HttpResponse execute(HttpClient client, HttpUriRequest request) {
		try {
			return client.execute(request);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
		return null;
	}

	/**
	 * 将gzip编码的HttpEntity转换为普通的HttpEntity
	 */
	public static HttpEntity decodeGZIPEntity(HttpEntity gzipEntity) {
		if (gzipEntity != null) {
			return new HttpEntityWrapper(gzipEntity) {
				@Override
				public InputStream getContent() throws IOException {
					if (super.wrappedEntity != null) {
						return new GZIPInputStream(super.wrappedEntity.getContent(), 8192);
					}
					return super.getContent();
				}
			};
		}
		return null;
	}


	/**
	 * 关闭链接
	 * @param client HttpClient对象
	 */
	public static void shutdown(HttpClient client) {
		if (client != null) {
			try {
				client.getConnectionManager().shutdown();
			}
			catch (Exception e) {
				LOGGER.error(e.getMessage(),e);
			}
		}
	}

	/**
	 * 销毁HttpResponse，保证实体内容被完全消耗而且低层的流被关闭。
	 * @param response HttpResponse对象
	 */
	public static void consume(HttpResponse response) {
		if (response != null) {
			try {
				EntityUtils.consume(response.getEntity());
			}
			catch (IOException e) {
				LOGGER.error(e.getMessage(),e);
			}
		}
	}

	/**
	 * 通过GET方法访问网络获取返回的字符串数据
	 * @param url 链接地址
	 * @return 成功返回服务器返回的字符串数据，失败返回null。
	 */
	public static String getContentFromURL(String url) {
		return getContentFromURL(url, null);
	}

	/**
	 * 通过GET方法访问网络获取返回的字符串数据
	 * @param url 链接地址
	 * @param charset 字符集编码
	 * @return 成功返回服务器返回的字符串数据，失败返回null。
	 */
	public static String getContentFromURL(String url, String charset) {
		HttpClient client = newHttpClient();
		HttpGet get = new HttpGet(url);
		addBasicHeaders(get, url);
		try {
			HttpResponse response = client.execute(get);
			return getContentFromResponse(response, charset);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
		return null;
	}

	public static void addBasicHeaders(HttpUriRequest request, String url) {
		request.addHeader("Connection", "Close");
		request.addHeader("Referer", url);
		request.addHeader("Host", getHost(url));
	}

	public static String getHost(String url) {
		try {
			URL u = new URL(url);
			return u.getHost();
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
		return null;
	}

	public static SchemeRegistry getSchemeRegistry() {
		KeyStore trustStore = null;
		SSLSocketFactory sslSocketFactory = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			sslSocketFactory = new MySSLSocketFactory(trustStore);
			sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		if (sslSocketFactory != null) {
			schemeRegistry.register(new Scheme("https", 443, sslSocketFactory));
		}
		else {
			schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		}

		return schemeRegistry;
	}

	/**
	 * 信任所有的SSL证书
	 */
	public static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");
		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);
			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket connectSocket(Socket sock, String host, int port,
				InetAddress localAddress, int localPort, HttpParams params)
				throws IOException {
			int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
			int soTimeout = HttpConnectionParams.getSoTimeout(params);

			InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
			SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

			if (localAddress != null || localPort > 0) {
				if (localPort < 0) {
					localPort = 0;
				}
				InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
				sslsock.bind(isa);
			}

			sslsock.connect(remoteAddress, connTimeout);
			sslsock.setSoTimeout(soTimeout);
			return sslsock;
		}
		@Override
		public boolean isSecure(Socket sock) throws IllegalArgumentException {
			return true;
		}
		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}
		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	/**
	 * 从HttpClient中获取服务器返回的Cookie
	 */
	public static String getCookie(HttpClient httpClient) {
		if (httpClient instanceof DefaultHttpClient) {
			return getCookie(((DefaultHttpClient)httpClient).getCookieStore());
		}
		return null;
	}

	/**
	 * 从cookiestore中获取服务器返回的cookie
	 */
	public static String getCookie(CookieStore cookieStore) {
		if (cookieStore != null) {
			List<Cookie> cookies = cookieStore.getCookies();
			if (cookies != null && cookies.size() > 0) {
				String name;
				String value;
				StringBuilder sb = new StringBuilder();
				for (Cookie cookie : cookies) {
					name = cookie.getName();
					value = cookie.getValue();
					if (name != null && value != null) {
						sb.append(name+"="+value+"; ");//cookie项之间用分号+空格隔开
					}
				}
				if (sb.length() > 1) {
					return sb.substring(0, sb.length()-2);
				}
			}
		}
		return null;
	}

	public static String formatCookieValue(String cookieValue) {
		String[] cookies = cookieValue.split(COOKIE_DIVIDER);
		StringBuilder sb = new StringBuilder();
		if (cookies.length > 0) {
			for (String cookie : cookies) {
				String lowerCookie = cookie.toLowerCase();
				if (lowerCookie.startsWith("path=") || lowerCookie.startsWith("expires=")
						|| lowerCookie.startsWith("domain=") || lowerCookie.startsWith("max-age=")
						|| lowerCookie.startsWith("httponly")) {
					continue;
				}
				sb.append(cookie.trim()).append(COOKIE_DIVIDER);
			}
		}
		String newCookie = sb.toString();
		if (newCookie.endsWith(COOKIE_DIVIDER)) {
			return newCookie.substring(0, sb.length()-COOKIE_DIVIDER.length());
		}
		return newCookie;
	}

	public static String getCookie(Header[] headers) {
		StringBuilder sb = new StringBuilder();
		if (headers != null && headers.length > 0) {
			for (Header header : headers) {
				if (RESPONSE_COOKIE_NAME.equalsIgnoreCase(header.getName())) {
					String value = ClientUtils.formatCookieValue(header.getValue());
					if (value != null && value.length() > 0) {
						sb.append(value).append(COOKIE_DIVIDER);
					}
				}
			}
		}
		String cookieText = sb.toString();
		if (cookieText.endsWith(COOKIE_DIVIDER)) {
			return cookieText.substring(0, cookieText.length()-COOKIE_DIVIDER.length());
		}
		return cookieText;
	}

	public static int CONNECTION_TIMEOUT = 10000;
	public static int SOCKET_TIMEOUT = 15000;

	public static BasicHttpParams getBasicHttpParams() {
		BasicHttpParams params = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(params, "brucezee");
		HttpProtocolParams.setContentCharset(params, "UTF-8");

		return params;
	}

	public static DefaultHttpClient newPoolingHttpClient(int maxConnections) {
		BasicHttpParams params = getBasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		PoolingClientConnectionManager clientConnectionManager = new PoolingClientConnectionManager(schemeRegistry);
		clientConnectionManager.setMaxTotal(maxConnections);
//		clientConnectionManager.setDefaultMaxPerRoute(maxConnections);
//		HttpHost localhost = new HttpHost("locahost", 80);
//		clientConnectionManager.setMaxPerRoute(new HttpRoute(localhost), maxConnections);
		return new DefaultHttpClient(clientConnectionManager, params);
	}

	public static CloseableHttpClient newSslHttpClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext)
					;
			return HttpClients.custom().setSSLSocketFactory(sslsf).setRedirectStrategy(new DefaultRedirectStrategy(){
				@Override
				public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) {
					int responseCode = response.getStatusLine().getStatusCode();
					if (responseCode == 301 || responseCode == 302) {
						return true;
					}
					return false;
				}
				protected boolean isRedirectable(final String method) {
					return true;
				}
			}).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}

//	public static DefaultHttpClient newSslHttpClient() {
//		BasicHttpParams params = getBasicHttpParams();
//		SSLSocketFactory ssf = null;
//		try {
//			SSLContext ctx = SSLContext.getInstance("TLS");
//			X509TrustManager tm = new X509TrustManager() {
//				public X509Certificate[] getAcceptedIssuers() {
//					return null;
//				}
//				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
//				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
//			};
//			ctx.init(null, new TrustManager[] { tm }, null);
//			ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		SchemeRegistry schemeRegistry = new SchemeRegistry();
//		if (ssf != null) {
//			schemeRegistry.register(new Scheme("https", 443, ssf));
//		}
//		else {
//			schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
//		}
//		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
//		return new DefaultHttpClient(params);
//	}

	public static DefaultHttpClient newHttpClient() {
		return newHttpClient(getBasicHttpParams());
	}

	public static DefaultHttpClient newHttpClient(HttpParams httpParams) {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
		DefaultHttpClient httpClient = new DefaultHttpClient(cm, httpParams);
		return httpClient;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 抓取网页
	 * @param url 链接地址
	 * @return 成功返回Jsoup的Document对象
	 */
	public static Document connect(String url) {
		return connect(url, null, null);
	}
	
	/**
	 * 抓取网页
	 * @param url 链接地址
	 * @param baseUri baseUri 网站的根路径（用于Jsoup解析超链接时获取完整路径）
	 * @return 成功返回Jsoup的Document对象
	 */
	public static Document connect(String url, String baseUri) {
		return connect(url, null, baseUri);
	}
	
	/**
	 * 抓取网页
	 * @param url 链接地址
	 * @param charset 字符编码
	 * @param baseUri 网站的根路径（用于Jsoup解析超链接时获取完整路径）
	 * @return 成功返回Jsoup的Document对象
	 */
	public static Document connect(String url, String charset, String baseUri) {
		try {
			String result = getContentFromURL(url, charset);
			if (baseUri != null) {
				return Jsoup.parse(result, baseUri);
			}
			
			return Jsoup.parse(result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 创建HttpContext
	 */
	public static HttpContext createHttpContext(CookieStore cookieStore) {
		HttpContext httpContext = HttpClientContext.create();
		if (cookieStore != null) {
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		}
		return httpContext;
	}
	
	/**
	 * 创建HttpContext
	 */
	public static HttpContext createHttpContext() {
		return createHttpContext(new BasicCookieStore());
	}
}
