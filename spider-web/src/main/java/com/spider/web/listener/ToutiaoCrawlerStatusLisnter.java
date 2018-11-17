package com.spider.web.listener;

import com.spider.core.webmagic.handler.StatusListener;
import com.spider.web.service.TouTiaoCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: wangpeng
 * @date: 2018/11/17 20:23
 */
@Component
public class ToutiaoCrawlerStatusLisnter implements StatusListener {

    @Autowired
    private TouTiaoCrawlerService crawlerService;


    /**
     * 汇报任务开始状态
     * @param task
     */
    @Override
    public void reportSoptEvent(String task) {
        //todo update task status
    }

    /**
     * 汇报任务结束状态
     * @param task
     */
    @Override
    public void reportStartEvent(String task) {
        //todo record task start status

    }

    @Override
    public void restartTask(String task) {
        crawlerService.startCrawler(task);
    }
}
