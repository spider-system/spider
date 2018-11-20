package com.spider.web.controller;
import com.google.common.collect.Maps;
import com.spider.business.repostory.mapper.HaohuoCommodityMapper;
import com.spider.business.repostory.mapper.HaohuoSellMapper;
import com.spider.business.repostory.plugin.PageHelper;
import com.spider.common.bean.HaohuoCommodity;
import com.spider.common.bean.HaohuoSell;
import com.spider.common.bean.Task;
import com.spider.common.response.ReturnT;
import com.spider.common.vo.HaohuoCommdityVO;
import com.spider.web.service.HaohuoCrawlerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangchangpeng on 2018/11/17.
 */
@Api(value = "haohuo商品详细销量管理",tags = "商品销量管理")
@RestController
@RequestMapping("haohuo")
public class HaohuoCrawlerController {

    @Autowired
    private HaohuoCrawlerService haohuoCrawlerService;

    @Autowired
    private HaohuoCommodityMapper haohuoCommodityMapper;

    @Autowired
    private HaohuoSellMapper haohuoSellMapper;

    @ApiOperation("开始执行爬取任务")
    @RequestMapping(value = "/task/start", method = RequestMethod.POST)
    public ReturnT start(@RequestParam @ApiParam(value = "商品ID") String productId ){
        return haohuoCrawlerService.startCrawler(productId);
    }

    @ApiOperation("更新全部商品的销量")
    @RequestMapping(value = "/task/crawlerAll", method = RequestMethod.GET)
    public ReturnT crawlerAll(){
        return haohuoCrawlerService.startAllCrawler();
    }


    @ApiOperation("查询全部商品销量")
    @RequestMapping(value = "product/list", method = RequestMethod.GET)
    public PageHelper.Page<HaohuoCommdityVO> list(HaohuoCommdityVO haohuoCommdityVO){
        PageHelper.startPage(haohuoCommdityVO.getPage(),haohuoCommdityVO.getPageSize());
        HaohuoCommodity commdity = new HaohuoCommodity();
        BeanUtils.copyProperties(haohuoCommdityVO, commdity);
        Map<String,String> extendedParameter = Maps.newHashMap();
        extendedParameter.put("sidx","create_time");
        extendedParameter.put("sord","desc");
        commdity.setExtendedParameter(extendedParameter);
        List<HaohuoCommodity> commodityList = haohuoCommodityMapper.query(commdity);

        List<HaohuoCommdityVO> haohuoCommdityVOS = new ArrayList<>();
        for (HaohuoCommodity haohuoCommodity : commodityList) {
            HaohuoCommdityVO vo = new HaohuoCommdityVO();
            BeanUtils.copyProperties(haohuoCommodity, vo);
            List<HaohuoSell> haohuoSells = haohuoSellMapper.selectByPrimaryKey(haohuoCommodity.getProductId());
            if (CollectionUtils.isEmpty(haohuoSells)) {
                vo.setLastSellNum(haohuoCommodity.getSellNum());
                vo.setTotalSellNum(haohuoCommodity.getSellNum());
                vo.setAddSellNum(0);
                vo.setCrawlerTime(haohuoCommodity.getCreateTime());
                vo.setLastCrawlerTime(haohuoCommodity.getCreateTime());
            } else {
                HaohuoSell haohuoSell = haohuoSells.get(0);
                vo.setLastSellNum(haohuoSell.getLastSellNum());
                vo.setTotalSellNum(haohuoSell.getTotalSellNum());
                vo.setAddSellNum(haohuoSell.getAddSellNum());
                vo.setCrawlerTime(haohuoSell.getCrawlerTime());
                vo.setLastCrawlerTime(haohuoSell.getLastCrawlerTime());
            }
            haohuoCommdityVOS.add(vo);
        }
        PageHelper.Page page = PageHelper.endPage();
        page.setResult(haohuoCommdityVOS);
        return page;
    }

}
