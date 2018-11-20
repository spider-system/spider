package com.spider.web.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.spider.business.repostory.mapper.TaskMapper;
import com.spider.business.repostory.plugin.PageHelper;
import com.spider.common.bean.Task;
import com.spider.common.constants.GlobConts;
import com.spider.common.response.ReturnT;
import com.spider.common.utils.SpiderStringUtils;
import com.spider.common.vo.TaskVo;
import com.spider.core.util.ToutiaoUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: wangpeng
 * @date: 2018/11/18 15:47
 */
@Service
public class TaskCrawlerService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TouTiaoCrawlerService crawlerService;

    public ReturnT getTaskList(TaskVo taskVo){
        Task task = new Task();
        if(taskVo != null){
            BeanUtils.copyProperties(taskVo,task);
        }
        Map<String,String> extendedParameter = Maps.newHashMap();
        extendedParameter.put("sidx","create_time");
        extendedParameter.put("sord","desc");
        task.setExtendedParameter(extendedParameter);
        List<Task> taskList = taskMapper.query(task);
        return new ReturnT().sucessData(taskList);
    }

    public PageHelper.Page<Task> getTaskByQuery(TaskVo taskVo){
        PageHelper.startPage(taskVo.getPage(),taskVo.getPageSize());
        Task task = new Task();
        BeanUtils.copyProperties(taskVo,task);
        Map<String,String> extendedParameter = Maps.newHashMap();
        extendedParameter.put("sidx","create_time");
        extendedParameter.put("sord","desc");
        task.setExtendedParameter(extendedParameter);
        taskMapper.query(task);
        return PageHelper.endPage();
    }

    public ReturnT addToutiaoCrawlerTask(String deviceName, String url){
        Map<String,String> parmasMap = ToutiaoUtil.parseUrl(url);
        String rootUrl = parmasMap.get(GlobConts.ROOT_URL_PREFIX);
        parmasMap.remove(GlobConts.ROOT_URL_PREFIX);
        Task task = new Task();
        task.setDeviceName(deviceName);
        //check is exist in db
        List<Task> list =  taskMapper.query(task);
        if(CollectionUtils.isNotEmpty(list)){
            return new ReturnT().failureData("设备【"+deviceName+"】已存在");
        }
        task.setDeviceId(parmasMap.get(GlobConts.DEVICE_id));
        task.setDeviceBrand(parmasMap.get(GlobConts.DEVICE_BRAND));
        task.setDevicePlatform(parmasMap.get(GlobConts.DEVICE_PLATORM));
        task.setDeviceType(parmasMap.get(GlobConts.DEVICE_TYPE));
        task.setRootUrl(rootUrl);
        task.setParams(JSON.toJSONString(parmasMap));
        task.setTaskId(SpiderStringUtils.uuidString());
        task.setCreateTime(new Date());
        taskMapper.insertSelective(task);
        return new ReturnT().sucessData(task);
    }

    public ReturnT updateToutiaoCrawlerTask(String deviceName,String taskId,String url){
        Task task = new Task();
        task.setTaskId(taskId);
        task.setDeviceName(deviceName);
        List<Task> list =  taskMapper.query(task);
        if(CollectionUtils.isEmpty(list)){
            return new ReturnT().failureData("设备【"+deviceName+"】taskId【"+taskId+"】不存在");
        }
        Map<String,String> parmasMap = ToutiaoUtil.parseUrl(url);
        String rootUrl = parmasMap.get(GlobConts.ROOT_URL_PREFIX);
        parmasMap.remove(GlobConts.ROOT_URL_PREFIX);
        task.setDeviceId(parmasMap.get(GlobConts.DEVICE_id));
        task.setDeviceBrand(parmasMap.get(GlobConts.DEVICE_BRAND));
        task.setDevicePlatform(parmasMap.get(GlobConts.DEVICE_PLATORM));
        task.setDeviceType(parmasMap.get(GlobConts.DEVICE_TYPE));
        task.setRootUrl(rootUrl);
        task.setParams(JSON.toJSONString(parmasMap));
        task.setTaskId(SpiderStringUtils.uuidString());
        taskMapper.updateByPrimaryKeySelective(task);
        return new ReturnT().successDefault();
    }


    public ReturnT addTaskAndStart(String deviceName, String url){
        ReturnT result = addToutiaoCrawlerTask(deviceName,url);
        if(!result.isSuccess()){
            return result;
        }
        Task task = (Task) result.getData();
        String taskId = task.getTaskId();
        if(StringUtils.isBlank(taskId)){
            return new ReturnT().failureData("taskId不存在");
        }
        crawlerService.startCrawler(taskId);
        return new ReturnT().successDefault();
    }


    public ReturnT getTaskById(Integer id){
        return new ReturnT().sucessData(taskMapper.selectByPrimaryKey(id));
    }
}
