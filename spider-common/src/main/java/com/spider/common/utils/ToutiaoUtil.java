package com.spider.common.utils;

import com.google.gson.JsonObject;
import org.springframework.util.DigestUtils;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Package: com.fishsaying.ce.util
 *
 * @Author: Ouyang
 * @Date: 2018/3/13
 */
public class ToutiaoUtil {
    public static Map<String,String> getAsCp(){
        String as = "479BB4B7254C150";
        String cp = "7E0AC8874BB0985";
        int t = (int) (new Date().getTime()/1000);
        String e = Integer.toHexString(t).toUpperCase();
        String i = DigestUtils.md5DigestAsHex(String.valueOf(t).getBytes()).toUpperCase();
        if (e.length()==8) {
            char[] n = i.substring(0,5).toCharArray();
            char[] a = i.substring(i.length()-5).toCharArray();
            StringBuilder s = new StringBuilder();
            StringBuilder r = new StringBuilder();
            for (int o = 0; o < 5; o++) {
                s.append(n[o]).append(e.substring(o,o+1));
                r.append(e.substring(o+3,o+4)).append(a[o]);
            }
            as = "A1" + s + e.substring(e.length()-3);
            cp = e.substring(0,3) + r + "E1";
        }
        Map<String,String> map = new HashMap<>();
        map.put("as",as);
        map.put("cp",cp);
        return map;
    }


    public static String fillParameterMap(String url, Map<String, String> parameterMap)
            throws Exception {
        if (url == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            String replaceMent = "${" + entry.getKey() + "}";
            while (url.contains(replaceMent)) {
                url = url.replace(replaceMent, entry.getValue());
            }
        }
        while (url.contains("${now}")) {
            url = url.replace("${now}", System.currentTimeMillis() + "");
        }
        if (url.matches(".+(\\$\\{.+\\}).*")) {
            //log.error("{} contains undefined parameters", url);
            throw new Exception("URL_PARAM_ERROR");
        }
        return url;
    }


    public static String contactUrlParams(Map<String,String> params){
        StringBuffer sb = new StringBuffer();
        if(params == null){
            return null;
        }
        for(Map.Entry<String,String> kv:params.entrySet()){
            String value = kv.getValue();
            String key = kv.getKey();
            if(!value.equals("${"+key+"}")){
                value = URLEncoder.encode(value);
            }
            sb.append(kv.getKey()).append("=").append(value).append("&");
        }
        if(!sb.toString().endsWith("&")){
            sb.append("&");
        }
        Map<String,String> ascpMap = getAsCp();
        sb.append("as=").append(ascpMap.get("as")).append("&cp=").append(ascpMap.get("cp"));
        return sb.toString();
    }
}
