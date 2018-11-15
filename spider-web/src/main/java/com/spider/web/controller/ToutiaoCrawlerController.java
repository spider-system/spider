package com.spider.web.controller;

import com.spider.common.response.ReturnT;
import com.spider.web.service.TouTiaoCrawlerService;
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
 * @date: 2018/11/15 10:26
 */
@Api(value = "头条爬虫管理",tags = "爬虫管理")
@RestController
@RequestMapping("toutiao/crawler")
public class ToutiaoCrawlerController {

    @Autowired
    private TouTiaoCrawlerService touTiaoCrawlerService;


    @ApiOperation("新增今日头条爬虫任务")
    @RequestMapping(value = "/load/url",method = RequestMethod.POST)
    public ReturnT add(@RequestParam @ApiParam(value = "爬取的url",required = true) String url, @RequestParam @ApiParam(value = "机器名称") String deviceName){
        return touTiaoCrawlerService.importCrawlerUrl(url);
    }


    @ApiOperation("开始执行爬取任务")
    @RequestMapping(value = "/task/start",method = RequestMethod.POST)
    public ReturnT start( @RequestParam @ApiParam(value = "机器名称") String deviceName){
        return touTiaoCrawlerService.startCrawler(deviceName);
    }


    @ApiOperation("停止爬取任务")
    @RequestMapping(value = "/task/stop",method = RequestMethod.POST)
    public ReturnT stop( @RequestParam @ApiParam(value = "机器名称") String deviceName){
        return touTiaoCrawlerService.stopCrawlerTask(deviceName);
    }


    @ApiOperation("查询爬取任务状态")
    @RequestMapping(value = "/task/status",method = RequestMethod.GET)
    public ReturnT status( @RequestParam @ApiParam(value = "机器名称") String deviceName){
        return touTiaoCrawlerService.getTaskStatsByTask(deviceName);
    }
}
