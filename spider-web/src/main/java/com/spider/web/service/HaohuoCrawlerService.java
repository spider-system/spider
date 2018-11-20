package com.spider.web.service;

import com.alibaba.fastjson.JSONObject;
import com.spider.business.repostory.mapper.HaohuoCommodityMapper;
import com.spider.business.repostory.mapper.HaohuoSellMapper;
import com.spider.common.bean.HaohuoCommodity;
import com.spider.common.bean.HaohuoSell;
import com.spider.common.enums.ResultCodeEnum;
import com.spider.common.response.ReturnT;
import com.spider.core.webmagic.downloader.HttpSwitchProxyDownloader;
import com.spider.core.webmagic.handler.ToutiaoAdDataHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.Date;
import java.util.List;

/**
 * @author: Wangchangpeng
 * @date: 2018/11/17.
 */
@Service
public class HaohuoCrawlerService implements ToutiaoAdDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HaohuoCrawlerService.class);

    @Autowired
    private HaohuoCommodityMapper haohuoCommodityMapper;

    @Autowired
    private HaohuoSellMapper haohuoSellMapper;

    private static String PRODUCT_URL = "https://haohuo.snssdk.com/product/ajaxstaticitem?id=";

    /**
     * 商品详情
      */
    private static final String PRODUCT_LINK = "https://haohuo.jinritemai.com/views/product/item?id=%s";

    private HttpSwitchProxyDownloader httpDownLoader = new HttpSwitchProxyDownloader();

    public ReturnT startCrawler(String productId){
        if (StringUtils.isBlank(productId)) {
            return new ReturnT().successDefault();
        }
        Request request = new Request();
        request.setUrl(PRODUCT_URL + productId);
        Task task = Site.me().setDomain(UrlUtils.getDomain(PRODUCT_URL + productId)).setTimeOut(10000).toTask();
        Page page = httpDownLoader.download(request, task);
        String rawText = page.getRawText();
        if (StringUtils.isBlank(rawText)) {
            return new ReturnT().failureData(ResultCodeEnum.FAILURE);
        }
        JSONObject jsonObject =JSONObject.parseObject(rawText);
        String msg = (String) jsonObject.get("msg");
        Object responeData = jsonObject.get("data");
        if (StringUtils.isNotBlank(msg) || responeData == null) {
            return new ReturnT().failureData(msg);
        }
        JSONObject data = (JSONObject) responeData;
        handlerHaohuoData(data);
        return new ReturnT().successDefault();
    }


    public ReturnT startAllCrawler(){
        //get spider
        List<HaohuoCommodity> commodities = haohuoCommodityMapper.query(new HaohuoCommodity());
        if (CollectionUtils.isEmpty(commodities)) {
            return new ReturnT().successDefault();
        }
        for (HaohuoCommodity haohuoCommodity : commodities) {
            startCrawler(haohuoCommodity.getProductId());
        }
        return new ReturnT().successDefault();
    }

    @Override
    public void sendToHaohuoCrawler(String productId) {
        this.startCrawler(productId);
    }

    private void handlerHaohuoData(JSONObject jsonObject) {
        String productId = (String) jsonObject.get("product_id");
        HaohuoCommodity commodity = haohuoCommodityMapper.selectByProductId(productId);
        List<HaohuoSell> haohuoSells = haohuoSellMapper.selectByPrimaryKey(productId);
        if (commodity != null) {
            HaohuoSell haohuoSell = new HaohuoSell();
            haohuoSell.setProductId(commodity.getProductId());
            haohuoSell.setName(commodity.getName());
            haohuoSell.setLastSellNum(commodity.getSellNum());
            Integer sellNum = (Integer) jsonObject.get("sell_num");
            haohuoSell.setTotalSellNum(sellNum);
            haohuoSell.setAddSellNum(sellNum - commodity.getSellNum());
            haohuoSell.setCrawlerTime(new Date());
            if (CollectionUtils.isNotEmpty(haohuoSells)) {
                haohuoSell.setLastCrawlerTime(haohuoSells.get(0).getCrawlerTime());
            } else {
                haohuoSell.setLastCrawlerTime(commodity.getCreateTime());
            }
            haohuoSellMapper.insert(haohuoSell);
            return;
        }
        HaohuoCommodity haohuoCommodity = convertHaohuoCommdity(jsonObject);
        haohuoCommodityMapper.insert(haohuoCommodity);
    }



    /**
     * 转换为对象
     *
     * @param jsonObject
     * @return
     */
    private HaohuoCommodity convertHaohuoCommdity (JSONObject jsonObject) {
        HaohuoCommodity commodity = new HaohuoCommodity();
        // 商品Id
        String productId = (String) jsonObject.get("product_id");
        // 商品名称
        String name = (String) jsonObject.get("name");
        // 销量
        Integer sellNum = (Integer) jsonObject.get("sell_num");
        // 店铺名
        String shopName = (String) jsonObject.get("shop_name");
        // 最高价
        Integer skuMaxPrice = (Integer) jsonObject.get("sku_max_price");
        // 打折价
        Integer discountPrice = (Integer) jsonObject.get("discount_price");
        commodity.setProductId(productId);
        commodity.setShopName(shopName);
        commodity.setName(name);
        commodity.setSellNum(sellNum);
        commodity.setSkuMaxPrice(skuMaxPrice);
        commodity.setDiscountPrice(discountPrice);
        commodity.setProductLink(String.format(PRODUCT_LINK, productId));
        return commodity;
    }

}
