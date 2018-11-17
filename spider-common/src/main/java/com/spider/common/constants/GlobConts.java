package com.spider.common.constants;

/**
 * 全局常量
 * Created by wangpeng on 2016/12/13.
 */
public class GlobConts {

    public static final String DEFAULT_FORMATTER_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认分页大小
     */
    public static final int DEFUALT_PAGE_SIZE = 10;

    /**
     * UserAgent
     */
    public static final String USER_AGENT = "user_agent";


    public static final String STORE_DATA_PATH = "/Users/wangpeng/Documents/webmagic-crawler-file";

    public static final int MAX_CRAWLER_TIME = 2*1000*60*60*24;//最多跑2天

    /**
     * 单个ip请求间隔，单位ms
     */
    public final static long TIME_INTERVAL = 1000;

    public final static long PROXY_ALIVE_TIME_MILLS = 1000*60*10;//存活10分钟

    public final static String TOUTIAO_URL_PREFIX = "snssdk";

}
