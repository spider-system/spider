package com.spider.core.webmagic.proxy.task;

import com.spider.common.constants.GlobConts;
import com.spider.common.utils.PropertieUtils;
import com.spider.core.parse.impl.RegexEditable;
import com.spider.core.webmagic.proxy.ProxyHttpClient;
import com.spider.core.webmagic.proxy.ProxyListPageParser;
import com.spider.core.webmagic.proxy.ProxyPool;
import com.spider.core.webmagic.proxy.entity.Proxy;
import com.spider.core.webmagic.proxy.site.ProxyListPageParserFactory;
import com.spider.core.webmagic.proxy.util.ProxyUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.utils.UrlUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.spider.core.webmagic.proxy.ProxyPool.proxyQueue;



/**
 * 下载代理网页并解析
 * 若下载失败，通过代理去下载代理网页
 */
public class ProxyPageTask implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(ProxyPageTask.class);
	protected String url;
	private boolean proxyFlag;//是否通过代理下载
	private Proxy currentProxy;//当前线程使用的代理
	private List<String> cookies;

	protected static ProxyHttpClient proxyHttpClient = ProxyHttpClient.getInstance();
	private ProxyPageTask(){

	}

	public ProxyPageTask(String url, boolean proxyFlag, List<String> cookies) {
		this.url = url;
		this.proxyFlag = proxyFlag;
		this.cookies = cookies;
	}

	public ProxyPageTask(String url, boolean proxyFlag){
		this.url = url;
		this.proxyFlag = proxyFlag;
	}
	public void run(){
		long requestStartTime = System.currentTimeMillis();
		Request request = new Request();
		try {
			Page page = null;
			request.setUrl(url);
			if(CollectionUtils.isNotEmpty(cookies)){
				for(String cookie : cookies){
					String name = StringUtils.substringBefore(cookie,"=");
					String value = StringUtils.substringBetween(cookie,name+"=",";");
					request.addCookie(name,value);
				}
			}
			page = proxyHttpClient.getWebPage(request, Site.me().setDomain(UrlUtils.getDomain(url)).toTask());
			int status = page.getStatusCode();
			long requestEndTime = System.currentTimeMillis();
			String logStr = Thread.currentThread().getName() + " " + getProxyStr(currentProxy) +
					"  executing request " + page.getUrl()  + " response statusCode:" + status +
					"  request cost time:" + (requestEndTime - requestStartTime) + "ms";
			if(status == HttpStatus.SC_OK){
				logger.debug(logStr);
				handle(page);
			} else {
				logger.error(logStr);
				Thread.sleep(100);
				List<String> cookies = page.getHeaders().get("Set-Cookie");
				if(status >= HttpStatus.SC_INTERNAL_SERVER_ERROR && CollectionUtils.isNotEmpty(cookies)){
					String cookieClearance = ProxyUtil.getYdclearanceCookie(page.getRawText());
					if(StringUtils.isNotEmpty(cookieClearance)){
						cookies.add(cookieClearance);
					}
				}
				retry(cookies);
			}
		} catch (InterruptedException e) {
			logger.error("InterruptedException", e);
		}finally {
			if(currentProxy != null){
				currentProxy.setTimeInterval(GlobConts.TIME_INTERVAL);
				proxyQueue.add(currentProxy);
			}
		}
	}

	/**
	 * retry
	 */
	public void retry(List<String> cookies){
		proxyHttpClient.getProxyDownloadThreadExecutor().execute(new ProxyPageTask(url, PropertieUtils.getBoolean("isProxy",true),cookies));
	}

	public void retry(){
		proxyHttpClient.getProxyDownloadThreadExecutor().execute(new ProxyPageTask(url, PropertieUtils.getBoolean("isProxy",true)));
	}

	public void handle(Page page){
		if (page.getRawText() == null || page.getRawText().equals("")){
			return;
		}

		ProxyListPageParser parser = ProxyListPageParserFactory.
				getProxyListPageParser(ProxyPool.proxyMap.get(url));
		List<Proxy> proxyList = parser.parse(page.getRawText());
		for(Proxy p : proxyList){
			p.setStartTime(System.currentTimeMillis());
			ProxyPool.lock.readLock().lock();
			boolean containFlag = ProxyPool.proxySet.contains(p);
			ProxyPool.lock.readLock().unlock();
			if (!containFlag){
				ProxyPool.lock.writeLock().lock();
				if(ProxyPool.proxySet.add(p)){
					proxyHttpClient.getProxyTestThreadExecutor().execute(new ProxyTestTask(p));
				}
				ProxyPool.lock.writeLock().unlock();
			}
		}
	}

	private String getProxyStr(Proxy proxy){
		if (proxy == null){
			return "";
		}
		return proxy.getIp() + ":" + proxy.getPort();
	}
}
