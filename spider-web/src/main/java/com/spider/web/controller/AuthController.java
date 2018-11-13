//package com.spider.web.controller;
//
//import com.spider.business.repostory.mapper.BannerMapper;
//import com.spider.business.repostory.plugin.PageHelper;
//import com.spider.business.repostory.plugin.PageHelper.Page;
//import com.spider.common.bean.Banner;
//import com.spider.common.response.ReturnT;
//import com.spider.common.utils.SpiderStringUtils;
//import java.util.List;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * Created by wangpeng on 2017/5/14.
// */
//@RestController
//@RequestMapping("auth")
//public class AuthController {
//
//
//    @Autowired
//    private BannerMapper bannerMapper;
//
//
//    @RequestMapping("getToken")
//    public ReturnT getAuthToken(){
//        return new ReturnT().sucessData(SpiderStringUtils.uuidString());
//    }
//
//
//    @RequestMapping("getBanner")
//    public Page<Banner> getBanner(){
//        PageHelper.startPage(1,20);
//        bannerMapper.query(new Banner());
//        return PageHelper.endPage();
//    }
//
//}
