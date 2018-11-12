package com.spider.core.webmagic.example;

import com.spider.common.enums.CategoryEnum;
import com.spider.common.utils.SpiderStringUtils;
import com.spider.core.webmagic.Page;
import com.spider.core.webmagic.Site;
import com.spider.core.webmagic.Spider;
import com.spider.core.webmagic.pipeline.TouTiaoPipeline;
import com.spider.core.webmagic.processor.PageProcessor;
import com.spider.core.webmagic.selector.JsonPathSelector;
import com.spider.core.webmagic.selector.RegexSelector;
import java.util.List;

/**
 * Created by wangpeng on 2017/5/15.
 */
public class TouTiaoPageProcessor implements PageProcessor {

    @Override
    public void process(Page page) {
        if(page.getRequest().getUrl().contains("api/pc/feed/")){
            parseSubResult(page);
        }else if(page.getRequest().getUrl().contains("/group/")){
            processContent(page);
        }
    }


    private void processContent(Page page){
        String title = page.getHtml().xpath("//h1[@class='article-title']/text()").toString();
        String type = page.getHtml().xpath("//span[@class='original']/text()").toString();
        String author = page.getHtml().xpath("//span[@class='src']/text()").toString();
        String time = page.getHtml().xpath("//span[@class='time']/text()").toString();
        String content = page.getHtml().$(".article-content").toString();;
        String category = page.getHtml().xpath("//a[@ga_event='click_channel']/text()").toString();
        page.putField("title",title);
        page.putField("type",type);
        page.putField("author",author);
        page.putField("content",content);
        page.putField("time",time);
        page.putField("category",category);
//        page.putField("rawText",page.getRawText());
        if(SpiderStringUtils.isEmpty(content)){
            page.setSkip(true);
        }
    }


    private void parseSubResult(Page page){
        String maxBeHotTime = page.getJson().regex("max_behot_time\":[\\s\\S](.*?)}",1).toString();
        if(SpiderStringUtils.isNotEmpty(maxBeHotTime)){
            page.addTargetRequest(processNewUrl(page,maxBeHotTime));
        }
        List<String> dataList = new JsonPathSelector("$.data").selectList(page.getRawText());
        if(!dataList.isEmpty()){
            for(String data : dataList){
                String tag = new JsonPathSelector("$.tag").select(data);// dataPage.getJson().jsonPath("$.chinese_tag").toString();
                if("ad".equals(tag)){//过滤广告
                    continue;
                }
                String source_url = new JsonPathSelector("$.source_url").select(data);
                if(SpiderStringUtils.isNotEmpty(source_url)){//增加爬取任务
                    page.addTargetRequest(ROOT.concat(source_url));
                }
            }
        }
        page.setSkip(true);//不保存
    }

    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    }

    private static final String ROOT = "http://www.toutiao.com";

    private static final String ROOT_URL = "http://www.toutiao.com/api/pc/feed/?category=news_tech&utm_source=toutiao&widen=1&max_behot_time=%s&max_behot_time_tmp=%s&tadrequire=true&as=A1E51991790C7E5&cp=59193CE72EF5FE1";
    private static final String SEED_URL = "http://www.toutiao.com/api/pc/feed/?category=%s&utm_source=toutiao&widen=1&max_behot_time=%s&max_behot_time_tmp=%s&tadrequire=true&as=A1E51991790C7E5&cp=59193CE72EF5FE1";

    public static void main(String[] args) {
        Spider.create(new TouTiaoPageProcessor())
            .addUrl(processSeedUrl())
            .addPipeline(new TouTiaoPipeline("/Users/wangpeng/Documents/webmagic-crawler-file"))
            .run();
    }

    private String processNewUrl(Page page,String maxBeHotTime){
        String hottime = new RegexSelector("max_behot_time=(.*?)&").select(page.getRequest().getUrl()).toString();
        return page.getRequest().getUrl().replaceAll(hottime,maxBeHotTime);
    }


    private static String[] processSeedUrl(){
        List<String> seedUrls = CategoryEnum.getCategoryList();
        String[] seedUrlArray = new String[seedUrls.size()];
        for (int i = 0; i < seedUrls.size(); i++) {
            seedUrlArray[i] = String.format(SEED_URL,seedUrls.get(i),0,0);
        }
        return seedUrlArray;
    }


}
