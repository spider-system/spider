package com.spider.web.controller;
import com.google.common.collect.Maps;
import com.spider.business.repostory.mapper.HaohuoCommodityMapper;
import com.spider.business.repostory.mapper.HaohuoSellMapper;
import com.spider.business.repostory.plugin.PageHelper;
import com.spider.common.bean.HaohuoCommodity;
import com.spider.common.bean.HaohuoSell;
import com.spider.common.response.ReturnT;
import com.spider.common.utils.ExcelUtil;
import com.spider.common.vo.HaohuoCommdityVO;
import com.spider.web.service.HaohuoCrawlerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangchangpeng on 2018/11/17.
 */
@Api(value = "haohuo商品详细销量管理",tags = "商品销量管理")
@RestController
@RequestMapping("haohuo")
public class HaohuoCrawlerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HaohuoCrawlerController.class);

    @Autowired
    private HaohuoCrawlerService haohuoCrawlerService;

    @Autowired
    private HaohuoCommodityMapper haohuoCommodityMapper;

    @Autowired
    private HaohuoSellMapper haohuoSellMapper;

    @ApiOperation("开始执行爬取任务")
    @RequestMapping(value = "/task/start", method = RequestMethod.GET)
    public ReturnT start(@RequestParam @ApiParam(value = "商品ID") Integer id ){
        HaohuoCommodity haohuoCommodity = haohuoCommodityMapper.selectByPrimaryKey(id);
        if (haohuoCommodity == null) {
            return new ReturnT().failureData("商品不存在！");
        }
        return haohuoCrawlerService.startCrawler(haohuoCommodity.getProductId());
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
        List<HaohuoCommdityVO> haohuoCommdityVOS = getHaohuoCommdityList(haohuoCommdityVO);
        PageHelper.Page page = PageHelper.endPage();
        page.setResult(haohuoCommdityVOS);
        return page;
    }

    @ApiOperation("导出商品销量xls")
    @RequestMapping(value = "/exportAllRecords", method = { RequestMethod.GET })
    public void exportAllRecords(HaohuoCommdityVO haohuoCommdityVO,
                                 HttpServletRequest request, HttpServletResponse response)
            throws IOException{
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");

        List<List<String>> rowList = getExportData(haohuoCommdityVO);
        String filename = "haohuo_" + System.currentTimeMillis() + ".xls";
        FileOutputStream out = new FileOutputStream(filename);
        HSSFWorkbook book = ExcelUtil.saveExcel(rowList);
        book.write(out);
        out.flush();
        out.close();
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename,"UTF-8"));
        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            OutputStream outputStreamo = response.getOutputStream();
            IOUtils.copy(fileInputStream, outputStreamo);
            outputStreamo.flush();
            outputStreamo.close();
            fileInputStream.close();
        } catch (Exception e) {
            LOGGER.error("haohuo sell records export error", e);
        }

    }

    private List<HaohuoCommdityVO> getHaohuoCommdityList(HaohuoCommdityVO haohuoCommdityVO) {
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

        return haohuoCommdityVOS;
    }


    private List<List<String>> getExportData(HaohuoCommdityVO haohuoCommdityVO) {
        List<HaohuoCommdityVO> haohuoCommdityList = getHaohuoCommdityList(haohuoCommdityVO);

        List<List<String>> rowList = new ArrayList<List<String>>();
        List<String> headList = Arrays.asList("一级行业", "二级行业","店铺名称","商品名称","商品最高价格","商品打折价格",
                "最新销量", "上次销量", "销量增量", "最近爬取时间","上次爬取时间","商品链接");
        rowList.add(headList);
        for (HaohuoCommdityVO vo : haohuoCommdityList) {
            List<String> colList = new ArrayList<String>();
            colList.add(vo.getFirstIndustry());
            colList.add(vo.getSecondIndustry());
            colList.add(vo.getShopName());
            colList.add(vo.getName());
            colList.add(vo.getSkuMaxPrice().toString());
            colList.add(vo.getDiscountPrice().toString());
            colList.add(vo.getTotalSellNum().toString());
            colList.add(vo.getLastSellNum().toString());
            colList.add(vo.getAddSellNum().toString());
            colList.add(format(vo.getLastCrawlerTime()));
            colList.add(format(vo.getCrawlerTime()));
            colList.add(vo.getProductLink());
            rowList.add(colList);
        }
        return rowList;
    }


    /**
     * 日期 => 字符串
     *
     * @param date
     * @return
     */
    private String format(Date date) {
        if(date == null){
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

}
