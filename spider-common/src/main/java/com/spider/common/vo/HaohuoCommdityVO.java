package com.spider.common.vo;

import com.spider.common.base.WebPageParameter;

import java.util.Date;

/**
 * Created by wangchangpeng on 2018/11/18.
 */
public class HaohuoCommdityVO extends WebPageParameter {


    private Integer id;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 一级行业
     */
    private String firstIndustry;

    /**
     * 二级行业
     */
    private String secondIndustry;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品最高价格
     */
    private Integer skuMaxPrice;

    /**
     * 商品打折价
     */
    private Integer discountPrice;

    /**
     * 商品链接
     */
    private String productLink;


    /**
     * '上一次爬取的销量'
     */
    private Integer lastSellNum;

    /**
     * '销量增量'
     */
    private Integer addSellNum;

    /**
     * '总销量'
     */
    private Integer totalSellNum;

    /**
     * 入库时间
     */
    private Date crawlerTime;

    /**
     * 上次爬取时间
     */
    private Date lastCrawlerTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getFirstIndustry() {
        return firstIndustry;
    }

    public void setFirstIndustry(String firstIndustry) {
        this.firstIndustry = firstIndustry;
    }

    public String getSecondIndustry() {
        return secondIndustry;
    }

    public void setSecondIndustry(String secondIndustry) {
        this.secondIndustry = secondIndustry;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSkuMaxPrice() {
        return skuMaxPrice;
    }

    public void setSkuMaxPrice(Integer skuMaxPrice) {
        this.skuMaxPrice = skuMaxPrice;
    }

    public Integer getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Integer discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getProductLink() {
        return productLink;
    }

    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }

    public Integer getLastSellNum() {
        return lastSellNum;
    }

    public void setLastSellNum(Integer lastSellNum) {
        this.lastSellNum = lastSellNum;
    }

    public Integer getAddSellNum() {
        return addSellNum;
    }

    public void setAddSellNum(Integer addSellNum) {
        this.addSellNum = addSellNum;
    }

    public Integer getTotalSellNum() {
        return totalSellNum;
    }

    public void setTotalSellNum(Integer totalSellNum) {
        this.totalSellNum = totalSellNum;
    }

    public Date getCrawlerTime() {
        return crawlerTime;
    }

    public void setCrawlerTime(Date crawlerTime) {
        this.crawlerTime = crawlerTime;
    }

    public Date getLastCrawlerTime() {
        return lastCrawlerTime;
    }

    public void setLastCrawlerTime(Date lastCrawlerTime) {
        this.lastCrawlerTime = lastCrawlerTime;
    }
}
