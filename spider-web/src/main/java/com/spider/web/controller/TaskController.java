package com.spider.web.controller;

import com.spider.business.repostory.plugin.PageHelper;
import com.spider.common.bean.Task;
import com.spider.common.response.ReturnT;
import com.spider.common.vo.TaskVo;
import com.spider.web.service.TaskCrawlerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: wangpeng
 * @date: 2018/11/18 16:37
 */
@Api(value = "爬虫任务管理",tags = "任务管理")
@RestController
@RequestMapping("task")
public class TaskController {

    @Autowired
    private TaskCrawlerService taskCrawlerService;


    @ApiOperation("导入爬虫任务")
    @RequestMapping(value = "/import/url",method = RequestMethod.POST)
    public ReturnT startTaskByUrl(@RequestParam @ApiParam(value = "爬取的url",required = true) String url, @RequestParam @ApiParam(value = "机器名称") String deviceName){
        return taskCrawlerService.addToutiaoCrawlerTask(deviceName,url);
    }

    @ApiOperation("导入爬虫任务并启动")
    @RequestMapping(value = "/start/url",method = RequestMethod.POST)
    public ReturnT startTaskByUrlAndStart(@RequestParam @ApiParam(value = "爬取的url",required = true) String url, @RequestParam @ApiParam(value = "机器名称") String deviceName){
        return taskCrawlerService.addTaskAndStart(deviceName,url);
    }


    @ApiOperation("查询任务列表，带分页")
    @RequestMapping(value = "page",method = RequestMethod.GET)
    public PageHelper.Page<Task> queryTaskByPage(@ApiParam(value = "查询对象") TaskVo taskVo){
        return taskCrawlerService.getTaskByQuery(taskVo);
    }


    @ApiOperation("查询所有任务列表")
    @RequestMapping(value = "list",method = RequestMethod.GET)
    public ReturnT queryAllTask(@ApiParam(value = "查询对象") TaskVo taskVo){
        return taskCrawlerService.getTaskList(taskVo);
    }


    @ApiOperation("根据id查询任务")
    @RequestMapping(value = "getById",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnT queryTaskById(@ApiParam(value = "id") Integer id){
        return taskCrawlerService.getTaskById(id);
    }


    @ApiOperation("更新爬虫任务")
    @RequestMapping(value = "/task/update",method = RequestMethod.POST)
    public ReturnT startTaskByUrl(@RequestParam @ApiParam(value = "爬取的url",required = true) String url, @RequestParam @ApiParam(value = "机器名称") String deviceName,@RequestParam @ApiParam(value = "任务Id") String taskId){
        return taskCrawlerService.updateToutiaoCrawlerTask(deviceName,taskId,url);
    }










}
