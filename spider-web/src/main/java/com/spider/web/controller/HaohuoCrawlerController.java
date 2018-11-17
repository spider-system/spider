package com.spider.web.controller;
import com.spider.business.repostory.mapper.HaohuoCommodityMapper;
import com.spider.business.repostory.mapper.HaohuoSellMapper;
import com.spider.common.bean.HaohuoCommodity;
import com.spider.common.bean.HaohuoSell;
import com.spider.common.response.ReturnT;
import com.spider.common.vo.HaohuoCommdityVO;
import com.spider.web.service.HaohuoCrawlerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangchangpeng on 2018/11/17.
 */
@Api(value = "haohuo商品详细销量管理",tags = "商品销量管理")
@RestController
@RequestMapping("haohuo/crawler")
public class HaohuoCrawlerController {

    @Autowired
    private HaohuoCrawlerService haohuoCrawlerService;

    @Autowired
    private HaohuoCommodityMapper haohuoCommodityMapper;

    @Autowired
    private HaohuoSellMapper haohuoSellMapper;

    @ApiOperation("开始执行爬取任务")
    @RequestMapping(value = "/product/start",method = RequestMethod.POST)
    public ReturnT start(@RequestParam @ApiParam(value = "机器名称") String deviceName,
                         @RequestParam @ApiParam(value = "商品ID") String productId ){
        return haohuoCrawlerService.startCrawler(deviceName, productId);
    }


    @ApiOperation("停止爬取任务")
    @RequestMapping(value = "/product/stop",method = RequestMethod.POST)
    public ReturnT stop( @RequestParam @ApiParam(value = "机器名称") String deviceName){
        return haohuoCrawlerService.stopCrawlerTask(deviceName);
    }


    @ApiOperation("查询爬取任务状态")
    @RequestMapping(value = "/product/status",method = RequestMethod.GET)
    public ReturnT status( @RequestParam @ApiParam(value = "机器名称") String deviceName){
        return haohuoCrawlerService.getTaskStatsByTask(deviceName);
    }


    @ApiOperation("更新全部商品的销量")
    @RequestMapping(value = "/product/crawlerAll",method = RequestMethod.GET)
    public ReturnT crawlerAll(@RequestParam @ApiParam(value = "机器名称") String deviceName){
        return haohuoCrawlerService.startAllCrawler(deviceName);
    }


    @ApiOperation("查询全部商品销量")
    @RequestMapping(value = "/product/list",method = RequestMethod.GET)
    public ReturnT list(){
        List<HaohuoCommdityVO> haohuoCommdityVOS = new ArrayList<>();
        List<HaohuoCommodity> commodityList = haohuoCommodityMapper.query(new HaohuoCommodity());
        if (CollectionUtils.isEmpty(commodityList)) {
            return new ReturnT().sucessData(haohuoCommdityVOS);
        }
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
        return new ReturnT().sucessData(haohuoCommdityVOS);
    }

}
