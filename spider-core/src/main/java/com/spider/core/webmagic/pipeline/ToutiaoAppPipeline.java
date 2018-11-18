package com.spider.core.webmagic.pipeline;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spider.core.parse.impl.RegexEditable;
import com.spider.core.webmagic.handler.ToutiaoAdDataHandler;
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

    private ToutiaoAdDataHandler dataHandler;

    public ToutiaoAppPipeline(ToutiaoAdDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public ToutiaoAppPipeline(String filePath, ToutiaoAdDataHandler dataHandler) {
        this.filePath = filePath;
        this.dataHandler = dataHandler;
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
                    try {
                        FileUtils.writeByteArrayToFile(new File(filePath+File.separator+"ad/"+task.getUUID()+"/"+System.currentTimeMillis()+".json"),content.getBytes());
                        String url = adJsonObject.getString("url");
                        if(StringUtils.isNoneBlank(url) && url.contains("haohuo")){
                            if(dataHandler != null){
                                String productId = new RegexEditable("id=(.*?)&").cutStr(url);
                                dataHandler.sendToHaohuoCrawler(productId);
                            }
                            FileUtils.writeByteArrayToFile(new File(filePath+File.separator+"ad/"+task.getUUID()+"/haohuo/"+System.currentTimeMillis()+".json"),content.getBytes());
                        }
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(),e);
                    }
                }
            }
        }
    }

}
