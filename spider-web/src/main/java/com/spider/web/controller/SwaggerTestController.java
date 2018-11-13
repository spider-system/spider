package com.spider.web.controller;

import com.spider.common.response.ReturnT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: wangpeng
 * @date: 2018/11/13 22:43
 */
@Api(value = "爬虫管理",tags = "爬虫管理")
@RestController
@RequestMapping("ce/crawler")
public class SwaggerTestController {

    @ApiOperation("新增今日头条爬虫任务")
    @RequestMapping(value = "/toutiao/add",method = RequestMethod.POST)
    public ReturnT add(@RequestParam @ApiParam(value = "uid",required = true) String uid, @RequestParam(defaultValue = "") @ApiParam(value = "max_behot_time") String maxBehotTime){
        return new ReturnT().successDefault();
    }
}
