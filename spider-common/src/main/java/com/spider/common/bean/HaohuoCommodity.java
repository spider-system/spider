package com.spider.common.bean;

import com.spider.common.base.BaseParameter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangchangpeng on 2018/11/14.
 */
public class HaohuoCommodity extends BaseParameter implements Serializable  {


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
     * 商品总销量
     */
    private Integer sellNum;

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
     * 商品状态
     */
    private Integer status;

    /**
     * 入库时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date UpdateTime;

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

    public Integer getSellNum() {
        return sellNum;
    }

    public void setSellNum(Integer sellNum) {
        this.sellNum = sellNum;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(Date updateTime) {
        UpdateTime = updateTime;
    }
}
