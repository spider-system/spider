package com.spider.core.webmagic.example;

import com.spider.core.webmagic.Page;
import com.spider.core.webmagic.Site;
import com.spider.core.webmagic.Spider;
import com.spider.core.webmagic.processor.PageProcessor;

/**
 * Created by wangpeng on 2017/5/29.
 */
public class TouTiaoApiPageProcessor implements PageProcessor {


    @Override
    public void process(Page page) {
        System.out.println(page.getRawText());
    }

    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(3).setTimeOut(10000).setSleepTime(1000);
    }


    private static final String FIRST_URL = "http://toutiao.com/api/article/recent/?source=2&category=news_hot&as=A1D5D87595C3287";

    public static void main(String[] args) {
        Spider.create(new TouTiaoApiPageProcessor())
            .addUrl(FIRST_URL)
            .run();;
    }
}
