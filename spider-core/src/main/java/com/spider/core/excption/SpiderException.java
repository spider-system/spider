package com.spider.core.excption;

/**
 * Created by wangpeng on 2016/12/14.
 */
public class SpiderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    public SpiderException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public SpiderException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
