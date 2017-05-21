package com.spider.business.repostory.mapper;

import com.spider.business.repostory.MyBatisRepository;
import com.spider.common.bean.Banner;
import java.util.List;

@MyBatisRepository
public interface BannerMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Banner record);

    int insertSelective(Banner record);

    Banner selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Banner record);

    int updateByPrimaryKey(Banner record);

    List<Banner> query(Banner banner);
}