package com.spider.core.webmagic.example;

import com.spider.core.parse.impl.JsonPathEditable;
import com.spider.core.parse.impl.RegexEditable;
import com.spider.core.webmagic.Page;
import com.spider.core.webmagic.Site;
import com.spider.core.webmagic.Spider;
import com.spider.core.webmagic.pipeline.TouTiaoPipeline;
import com.spider.core.webmagic.processor.PageProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by wangpeng on 2017/12/10.
 */
public class jjwxcPageProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        if(page.getUrl().get().contains("topten")){//首页
            processSeedPage(page);
        }else if(page.getUrl().get().contains("onebook")){//小说目录页
            processNovelPage(page);
        }

    }



    private void processNovelPage(Page page){
        if(page.getUrl().get().contains("chapterid")){//章节
            processNovelChapter(page);
            return;
        }
        String content = page.getRawText();
        Document document = Jsoup.parse(content);
        Elements elements = document.select("tr:contains(章节)~tr");
        for(Element element : elements){
            Elements tdEls = element.select("td");
            if(CollectionUtils.isNotEmpty(tdEls) && tdEls.size() == 6){
                Element chapterEl = tdEls.get(1).select("a").first();
                String title = chapterEl.text();
                String link = chapterEl.attr("href");
                page.putField("title",title);
                page.addTargetRequest(link);
            }
        }
    }


    private void processNovelChapter(Page page){
        String content = page.getRawText();
        Document document = Jsoup.parse(content);
        Element noveltextEl = document.select(".noveltext").first();
        String tilte = noveltextEl.select("h2").first().text();
        page.putField("tilte",tilte);
        page.putField("content",noveltextEl.html());
    }

    private void processSeedPage(Page page){
        String content = page.getRawText();
        ///html/body/table[3]/tbody/tr[1]/td[1]/div
        Document document = Jsoup.parse(content);
        Elements elements =  document.select("tr:contains(进度)~tr");
        for(Element element : elements){
            Elements tdEls = element.select("td");
            if(CollectionUtils.isNotEmpty(tdEls) && tdEls.size() == 9){
                String author = tdEls.get(1).text().trim();
                String novelName = tdEls.get(2).text().trim();
                String novelid = new RegexEditable("novelid=(\\d+)").cutStr(tdEls.get(2).html());
                Element novelLinkEl = tdEls.get(2).select("a").first();
                String href = novelLinkEl.attr("href");
                System.out.println("author="+author+",novelName="+novelName+",novelid="+novelid+",href="+href);
                page.addTargetRequest(ROOT_URL.concat(href));
            }
        }
        //System.out.println(elements);

    }

    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000).setCharset("GBK");
    }


    private static final String ROOT_URL = "http://www.jjwxc.net/";

    private static final String SEED_URL = "http://www.jjwxc.net/topten.php?orderstr=3";

    private static final String SEED_URL1 = "http://www.jjwxc.net/onebook.php?novelid=3400913&chapterid=1";

    public static void main(String[] args) {
        Spider.create(new jjwxcPageProcessor())
                .addUrl(SEED_URL)
                .addPipeline(new TouTiaoPipeline("/Users/wangpeng/Documents/webmagic-crawler-file")).run();
    }
}
