package com.spider.web.controller;

import com.spider.common.response.ReturnT;
import com.spider.common.utils.SpiderStringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wangpeng on 2017/5/14.
 */
@RestController
@RequestMapping("auth")
public class AuthController {


    @RequestMapping("getToken")
    public ReturnT getAuthToken(){
        return new ReturnT().sucessData(SpiderStringUtils.uuidString());
    }

}
