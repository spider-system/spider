package com.spider.core.webmagic.handler;


/**
 * @author: wangpeng
 * @date: 2018/11/17 20:18
 */
public interface StatusListener {

    void reportSoptEvent(String task);

    void reportStartEvent(String task);

    void restartTask(String task);
}
