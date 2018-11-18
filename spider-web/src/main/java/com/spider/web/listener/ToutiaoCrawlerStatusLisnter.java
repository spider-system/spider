package com.spider.web.listener;

import com.spider.business.repostory.mapper.TaskMapper;
import com.spider.common.bean.Task;
import com.spider.common.constants.GlobConts;
import com.spider.core.webmagic.handler.StatusListener;
import com.spider.web.service.TouTiaoCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author: wangpeng
 * @date: 2018/11/17 20:23
 */
@Component
public class ToutiaoCrawlerStatusLisnter implements StatusListener {

    @Autowired
    private TouTiaoCrawlerService crawlerService;

    @Autowired
    private TaskMapper taskMapper;


    /**
     * 汇报任务结束状态
     * @param taskId
     * @param startTime
     */
    @Override
    public void reportSoptEvent(String taskId,Date startTime) {
        Task task = taskMapper.queryByTaskId(taskId);
        if(task == null){
            return;
        }
        task.setStatus(GlobConts.SPIDER_STATUS_STOPPED);
        if(startTime != null){
            task.setLastCrawlerTime(startTime);
        }else {
            task.setLastCrawlerTime(task.getCrawlerTime());
        }
        task.setCrawlerTime(null);
        taskMapper.updateByPrimaryKeySelective(task);
    }

    /**
     * 汇报任务开始状态
     * @param taskId
     */
    @Override
    public void reportStartEvent(String taskId) {
        Task task = taskMapper.queryByTaskId(taskId);
        if(task == null){
            return;
        }
        task.setStatus(GlobConts.SPIDER_STATUS_RUNNING);
        task.setCrawlerTime(new Date());
        taskMapper.updateByPrimaryKeySelective(task);

    }

    @Override
    public void restartTask(String task) {
        crawlerService.startCrawler(task);
    }
}
