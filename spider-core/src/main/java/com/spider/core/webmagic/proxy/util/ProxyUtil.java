package com.spider.core.webmagic.proxy.util;


import com.spider.common.constants.GlobConts;
import com.spider.core.parse.impl.RegexEditable;
import com.spider.core.webmagic.proxy.entity.Proxy;
import org.apache.commons.lang3.StringUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    public static String getYdclearanceCookie(String body){
        String function = body.substring(body.indexOf("function"),body.lastIndexOf("}")+1);
        function = function.replace("eval(\"qo=eval;qo(po);\")", "return po");
        //提取js函数参数(正则表达式匹配)
        String regx ="setTimeout\\(\\\"\\D+\\((\\d+)\\)\"";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(body);
        String call = "";
        String param ="";
        while(matcher.find()){
            String group =matcher.group();
            param = group.substring(group.lastIndexOf("(")+1,group.indexOf(")"));
            call = group.substring(group.indexOf("\"")+1,group.lastIndexOf("("));
        }
        if(StringUtils.isEmpty(param) || StringUtils.isEmpty(call)){
            return null;
        }
        Map<String,String> cookies = new HashMap<>();
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("js");
        try
        {
            se.eval(function);
            Invocable inv2 = (Invocable) se;
            String result =  (String)inv2.invokeFunction(call,param);
            String cookie = new RegexEditable("document.cookie='(.*?)';").cutStr(result);
            return cookie;
        }
        catch(Exception e)
        {
            return null;
        }
    }
}
