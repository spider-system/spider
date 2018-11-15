package com.spider.core.webmagic.pipeline;

import com.spider.common.utils.SpiderStringUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

/**
 * Created by wangpeng on 2017/5/21.
 */
public class TouTiaoPipeline extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * create a FilePipeline with default path"/data/webmagic/"
     */
    public TouTiaoPipeline() {
        setPath("/data/webmagic/");
    }

    public TouTiaoPipeline(String path) {
        setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR;
        String category = processCategory(resultItems);
        if(SpiderStringUtils.isNotEmpty(category)){
            path = path+PATH_SEPERATOR+category+PATH_SEPERATOR;
        }
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getFile(path + DigestUtils
                .md5Hex(resultItems.getRequest().getUrl()) + ".html")),"UTF-8"));
            printWriter.println("url:\t" + resultItems.getRequest().getUrl());
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                if (entry.getValue() instanceof Iterable) {
                    Iterable value = (Iterable) entry.getValue();
                    printWriter.println(entry.getKey() + ":");
                    for (Object o : value) {
                        printWriter.println(o);
                    }
                } else {
                    printWriter.println(entry.getKey() + ":\t" + entry.getValue());
                }
            }
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }

    private String processCategory(ResultItems resultItems){
        Object catogroy = resultItems.getAll().get("category");
        if(catogroy == null){
            return null;
        }
        return (String)catogroy;
    }
}
