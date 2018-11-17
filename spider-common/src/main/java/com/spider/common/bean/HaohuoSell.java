package com.spider.common.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangchangpeng on 2018/11/17.
 */
public class HaohuoSell implements Serializable {


    private Integer id;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 商品名称
     */
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
