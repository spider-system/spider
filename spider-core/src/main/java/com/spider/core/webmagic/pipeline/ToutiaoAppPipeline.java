package com.spider.core.webmagic.pipeline;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spider.core.webmagic.ResultItems;
import com.spider.core.webmagic.Task;
import com.spider.core.webmagic.utils.FilePersistentBase;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

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
                        FileUtils.writeByteArrayToFile(new File(filePath+File.separator+System.currentTimeMillis()+".json"),content.getBytes());
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(),e);
                    }
                }
            }
        }
    }

}
