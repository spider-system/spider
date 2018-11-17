package com.spider.core.webmagic.pipeline;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.*;

/**
 * @author: wangpeng
 * @date: 2018/11/13 23:32
 */
public class ToutiaoAppPipeline extends FilePersistentBase implements Pipeline {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());


    private String filePath;

    public ToutiaoAppPipeline() {
    }

    public ToutiaoAppPipeline(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        JSONArray jsonArray = resultItems.get("data");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String content = jsonObject.getString("content");
            if(StringUtils.isNotEmpty(content) && content.contains("广告")){
                JSONObject adJsonObject = JSON.parseObject(content);
                String lable = adJsonObject.getString("label");
                if("广告".equals(lable)){
                    //record data
                    try {
                        FileUtils.writeByteArrayToFile(new File(filePath+File.separator+"ad/"+System.currentTimeMillis()+".json"),content.getBytes());
                        if(content.contains("haohuo")){
                            FileUtils.writeByteArrayToFile(new File(filePath+File.separator+"ad/haohuo/"+System.currentTimeMillis()+".json"),content.getBytes());
                        }
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(),e);
                    }
                }
            }
        }
    }

}
