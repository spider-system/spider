package com.spider.common.base;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wangpeng on 2016/11/7.
 */
public class BaseParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    private String callback;

    public Object extendedParameter;

    public Object getExtendedParameter() {
        return extendedParameter;
    }

    public void setExtendedParameter(Object extendedParameter) {
        this.extendedParameter = extendedParameter;
    }

    public void putExtendedParameterValue(String key, Object value) {
        if(this.extendedParameter == null) {
            this.extendedParameter = new LinkedHashMap();
        }

        if(!(this.extendedParameter instanceof Map)) {
            throw new IllegalArgumentException("extendedParameter不是Map类型，不能用当前方法添加扩展参数");
        } else {
            ((Map)this.extendedParameter).put(key, value);
        }
    }

    public Object getExtendedParameterValue(String key) {
        if(!(this.extendedParameter instanceof Map)) {
            throw new IllegalArgumentException("extendedParameter不是Map类型，不能用当前方法获取扩展参数");
        } else {
            return ((Map)this.extendedParameter).get(key);
        }
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }
}
