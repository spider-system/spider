package com.spider.core.util;

import com.spider.common.constants.GlobConts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import us.codecraft.webmagic.Page;

import java.net.URLDecoder;
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
        int t = (int) (System.currentTimeMillis()/1000);
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
        map.put(GlobConts.AS,as);
        map.put(GlobConts.CP,cp);
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
            throw new Exception("URL_PARAM_ERROR");
        }
        return url;
    }

    public static String replaceParams(Page page, Map<String,String> replaceMap) {
        String url = page.getUrl().get();
        Map<String,String> ascp = getAsCp();
        replaceMap.put(GlobConts.AS,ascp.get(GlobConts.AS));
        replaceMap.put(GlobConts.CP,ascp.get(GlobConts.CP));
        for (Map.Entry<String, String> kv : replaceMap.entrySet()) {
            String key = kv.getKey();
            String vaule = kv.getValue();
            String originalValue = page.getUrl().regex(key+"=(.*?)&").get();
            if(StringUtils.isNoneBlank(originalValue)){
                url = url.replace(key+"="+originalValue+"&",key+"="+vaule+"&");
            }
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
        sb.append(GlobConts.AS+"=").append(ascpMap.get(GlobConts.AS)).append("&"+GlobConts.CP+"=").append(ascpMap.get(GlobConts.AS));
        return sb.toString();
    }


    public static Map<String,String> parseUrl(String url){
        url = URLDecoder.decode(url);
        String[] arrs = url.split("\\?");
        String[] params = arrs[1].split("&");
        Map<String,String> parameters = new HashMap<>();
        for (String param : params) {
            String[] kv = param.split("=");
            String key = kv[0];
            String value = "";
            if(kv.length == 2){
                value = kv[1];
            }
            if(GlobConts.AS.equals(key) || GlobConts.CP.equals(key)){
                continue;
            }
            if(GlobConts.LAST_FRESH_SUB_ENTRANCE_INTERVAL.equals(key) || GlobConts.MIN_BEHOT_TIME.equals(key)
                    ||  GlobConts.LIST_COUNT.equals(key) ||  GlobConts.TT_FROM.equals(key)){
                value = "${"+key+"}";
            }
            parameters.put(key,value);
        }
        parameters.put(GlobConts.ROOT_URL_PREFIX,arrs[0]);
        return parameters;
    }
}
