package com.spider.core.webmagic.handler;


import java.util.Date;

/**
 * @author: wangpeng
 * @date: 2018/11/17 20:18
 */
public interface StatusListener {

    void reportSoptEvent(String task, Date startTime);

    void reportStartEvent(String task);

    void restartTask(String task);
}
