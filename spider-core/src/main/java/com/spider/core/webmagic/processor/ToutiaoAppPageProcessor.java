package com.spider.core.webmagic.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spider.common.constants.GlobConts;
import com.spider.core.util.ToutiaoUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: wangpeng
 * @date: 2018/11/13 22:51
 */
public class ToutiaoAppPageProcessor implements PageProcessor {


    @Override
    public void process(Page page) {
        processResult(page);
    }


    private void processResult(Page page){
        JSONObject jsonObject = JSON.parseObject(page.getRawText());
        boolean hasMoreDate = jsonObject.getBoolean("has_more");
        if(hasMoreDate){
            //add next url
            Integer total_number = jsonObject.getInteger("total_number");
            String listCountString = page.getUrl().regex("list_count=(\\d+)&").get();
            Integer listCount = Integer.parseInt(listCountString);
            Integer nextListCount = listCount+total_number;
            Long time = System.currentTimeMillis();
            Long min_behot_time = time - 1000*60*60;
            String device_platform = page.getUrl().regex("device_platform=(.*?)&").get();
            Map<String,String> replaceParam = new HashMap<>();
            replaceParam.put("min_behot_time",String.valueOf(min_behot_time));
            replaceParam.put("last_refresh_sub_entrance_interval",String.valueOf(time));
            if(GlobConts.ANDROID.equalsIgnoreCase(device_platform)){
                replaceParam.put("loc_time",String.valueOf(time));
                replaceParam.put("tt_from",String.valueOf(GlobConts.MORE_TT_FROM_ANDRIOD));
            }else {
                replaceParam.put("tt_from",String.valueOf(GlobConts.MORE_TT_FROM_IOS));
            }
            replaceParam.put("list_count",String.valueOf(nextListCount));
            String url = ToutiaoUtil.replaceParams(page,replaceParam);
            page.addTargetRequest(url);
        }else {
            page.setSkip(true);
        }
        page.putField("data",jsonObject.getJSONArray("data"));
    }

    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(3).setTimeOut(15000).setSleepTime(500);
    }

}
