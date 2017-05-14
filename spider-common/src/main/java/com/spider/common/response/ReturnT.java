package com.spider.common.response;


import com.spider.common.enums.ResultCodeEnum;
import java.io.Serializable;

/**
 * 响应返回对象
 * Created by wangpeng on 2016/12/13.
 */
public class ReturnT<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private T data;
    private boolean success;

    public ReturnT() {
    }

    public ReturnT(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public ReturnT(T data) {
        this.code = 200;
        this.data = data;
    }

    public ReturnT(int code, String msg, T data, boolean success) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }



    public ReturnT sucessData(T data){
        this.assignEnums(ResultCodeEnum.SUCCESS);
        this.data = data;
        return this;
    }

    public ReturnT failureData(ResultCodeEnum codeEnum){
        return failureData(codeEnum,null);
    }

    public ReturnT failureData(String msg){
        return failureData(ResultCodeEnum.FAILURE.getCode(),msg);
    }

    public ReturnT failureData(int code,String msg){
        this.success = false;
        this.msg = msg;
        this.code = code;
        return this;
    }

    public ReturnT failureData(ResultCodeEnum codeEnum,T data){
        this.assignEnums(codeEnum);
        this.data = data;
        return this;
    }

    public ReturnT assingData(ResultCodeEnum codeEnum,T data){
        this.assignEnums(codeEnum);
        this.data = data;
        return this;
    }

    public ReturnT successDefault(){
        this.assignEnums(ResultCodeEnum.SUCCESS);
        return this;
    }



    private void assignEnums(ResultCodeEnum codeEnum){
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMessage();
        this.success = codeEnum.isSuccess();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
