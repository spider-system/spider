package com.spider.core.webmagic.proxy.util;


import com.spider.common.constants.GlobConts;
import com.spider.core.webmagic.proxy.entity.Proxy;

public class ProxyUtil {
    /**
     * 是否丢弃代理
     * 失败次数大于３，且失败率超过60%，丢弃
     */
    public static boolean isDiscardProxy(Proxy proxy){
        int succTimes = proxy.getSuccessfulTimes();
        int failTimes = proxy.getFailureTimes();
        if(failTimes >= 3){
            double failRate = (failTimes + 0.0) / (succTimes + failTimes);
            if (failRate > 0.6){
                return true;
            }
        }
        //清除超时代理
        return false;
    }


    /**
     * 代理存活时间是否过长,过长丢弃代理
     * @param proxy
     * @return
     */
    public static boolean isAliveTooLong(Proxy proxy){
        Long startTime = proxy.getStartTime();
        Long times = System.currentTimeMillis()-startTime;
        return times >= GlobConts.PROXY_ALIVE_TIME_MILLS;

    }
}
