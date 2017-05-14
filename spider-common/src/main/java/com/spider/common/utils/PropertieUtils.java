package com.spider.common.utils;

import java.io.IOException;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * 读取Properties资源文件工具类
 * Created by wangpeng on 2016/9/29.
 */
public class PropertieUtils extends PropertyPlaceholderConfigurer {

    private static Properties prop = null;

    private static final String DEFAULT_CLASSPATH_PROPERTIES = "classpath*:config/*.properties";
    private static final String DEFAULT_CLASSPATH_ROOT_PROPERTIES = "classpath*:*.properties";

    public static Properties getProp(){
        if(prop == null){
            init();
        }
        return prop;
    }

    public static String getString(String key){
        return getString(key,null);
    }


    public static String getString(String key,String defualValue){
        return getProp().getProperty(key,defualValue);
    }

    public static Integer getInt(String key){
        return getInt(key,null);
    }

    public static Integer getInt(String key,Integer defualValue){
        String value = getString(key);
        return StringUtils.isNoneBlank(value) ? Integer.parseInt(value) : defualValue;
    }

    public static Long getLong(String key,Long defaultValue){
        String value = getString(key);
        return StringUtils.isNoneBlank(value) ? Long.parseLong(value) : defaultValue;
    }

    public static Long getLong(String key){
        return getLong(key);
    }

    public static Boolean getBoolean(String key,boolean defualValue){
        String value = getString(key);
        return  StringUtils.isNoneBlank(value) ? value.equalsIgnoreCase(DEFUALT_BOOLEAN_VALUE) : defualValue;
    }

    public static Boolean getBoolean(String key){
        return getBoolean(key,true);
    }

    private static final String DEFUALT_BOOLEAN_VALUE = "true";




    public static synchronized void init(){
        if(prop == null || prop.size()<=0){
            prop = new Properties();
            loadPropByPath(DEFAULT_CLASSPATH_PROPERTIES);
            loadPropByPath(DEFAULT_CLASSPATH_ROOT_PROPERTIES);
        }
    }



    private static void loadPropByPath(String path){
        if(path.startsWith("classpath*:")){
            try {
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                Resource[] resources = resolver.getResources(path);
                if(resources != null && resources.length > 0){
                    for(Resource resource : resources){
                        if(resource.isReadable()){
                            PropertiesLoaderUtils.fillProperties(prop, resource);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
