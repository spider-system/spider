package com.spider.business.repostory.mapper;

import com.spider.business.repostory.MyBatisRepository;
import com.spider.common.bean.HaohuoSell;

import java.util.List;

@MyBatisRepository
public interface HaohuoSellMapper {

    int insert(HaohuoSell record);

    List<HaohuoSell> selectByPrimaryKey(String productId);

    int updateByPrimaryKeySelective(HaohuoSell record);

}