package com.spider.common.utils;

import java.util.UUID;

/**
 * Created by wangpeng on 2016/11/4.
 */
public class SpiderStringUtils {


    /**
     * 获取uuid
     * @return
     */
    public static String uuidString(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

   public static boolean isEmpty(String text){
       return (text == null || "".equals(text) ) ? true : false;
   }

    public static boolean isNotEmpty(String text){
        return !isEmpty(text);
    }
}
