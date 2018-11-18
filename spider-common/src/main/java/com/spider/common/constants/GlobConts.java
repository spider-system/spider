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

    public static final Long MAX_CRAWLER_TIME = 2*1000*60*60*24L;//最多跑2天
    public static final Long CRAWLER_TIME_TOO_SHORT = 1000*60*60L;//最多跑1天
    public static final Long CRAWLER_TIME_REGION = 1000*60*60L;//最多跑2天

    /**
     * 单个ip请求间隔，单位ms
     */
    public final static long TIME_INTERVAL = 1000;

    public final static long PROXY_ALIVE_TIME_MILLS = 1000*60*10;//存活10分钟

    public final static String TOUTIAO_URL_PREFIX = "snssdk";

    public final static int SPIDER_STATUS_INIT = 0;
    public final static int SPIDER_STATUS_RUNNING = 1;
    public final static int SPIDER_STATUS_STOPPED = 2;



    public static final String FIRST_FF_FROM_ANDRIOD = "enter_auto";
    public static final String FIRST_FF_FROM_IOS = "enter_auto";
    public static final String MORE_TT_FROM_ANDRIOD = "pre_load_more";
    public static final String MORE_TT_FROM_IOS = "load_more";
    public static final String PULL_TT_FROM = "pull";
    public static final String LAST_READ_TT_FROM = "last_read";

    public static final String ANDROID = "android";

    public static final String ROOT_URL_PREFIX = "root_url";
    public static final String DEVICE_id = "device_id";
    public static final String DEVICE_PLATORM = "device_platform";
    public static final String DEVICE_TYPE = "device_type";
    public static final String DEVICE_BRAND = "device_brand";
    public static final String LAST_FRESH_SUB_ENTRANCE_INTERVAL = "last_refresh_sub_entrance_interval";
    public static final String MIN_BEHOT_TIME = "min_behot_time";
    public static final String LIST_COUNT = "list_count";
    public static final String TT_FROM = "tt_from";
    public static final String LOC_TIME = "loc_time";
    public static final String AS = "as";
    public static final String CP = "cp";
    public static final String HAS_MORE = "has_more";
    public static final String TOTAL_NUMBER = "total_number";

}
