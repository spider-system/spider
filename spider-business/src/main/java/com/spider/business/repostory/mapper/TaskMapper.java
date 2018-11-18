package com.spider.business.repostory.mapper;

import com.spider.business.repostory.MyBatisRepository;
import com.spider.common.bean.Task;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisRepository
public interface TaskMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Task record);

    int insertSelective(Task record);

    Task selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Task record);

    int updateByPrimaryKey(Task record);

    List<Task> query(Task task);

    Task queryByTaskId(@Param("taskId") String taskId);
}