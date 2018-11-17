package com.spider.core.webmagic.pipeline;

import com.alibaba.fastjson.JSONObject;
import com.spider.business.repostory.mapper.HaohuoCommodityMapper;
import com.spider.business.repostory.mapper.HaohuoSellMapper;
import com.spider.common.bean.HaohuoCommodity;
import com.spider.common.bean.HaohuoSell;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.util.Date;
import java.util.List;

/**
 * Created by wangchangpeng on 2018/11/14.
 */
public class HaohuoApiPipeline extends FilePersistentBase implements Pipeline {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    // 商品详情
    private static final String PRODUCT_LINK = "https://haohuo.jinritemai.com/views/product/item?id=%s";

    @Autowired
    private HaohuoCommodityMapper haohuoCommodityMapper;

    @Autowired
    private HaohuoSellMapper haohuoSellMapper;

    @Override
    public void process(ResultItems resultItems, Task task) {
        JSONObject jsonObject = resultItems.get("data");

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
            if (CollectionUtils.isEmpty(haohuoSells)) {
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
