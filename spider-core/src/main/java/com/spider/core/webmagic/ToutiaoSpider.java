package com.spider.core.webmagic;

import com.spider.core.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author: wangpeng
 * @date: 2018/11/17 18:51
 */
public class ToutiaoSpider extends Spider {
    /**
     * create a spider with pageProcessor.
     *
     * @param pageProcessor pageProcessor
     */
    public ToutiaoSpider(PageProcessor pageProcessor) {
        super(pageProcessor);
    }

    public static ToutiaoSpider create(PageProcessor pageProcessor) {
        return new ToutiaoSpider(pageProcessor);
    }


    @Override
    public void run() {
        this.beforeRun();
        super.run();
        this.beforeDestory();
    }


    public void beforeRun(){

    }

    public void beforeDestory(){
        SpiderMonitor.instance().unRegisterSpdierByTask(getUUID());
    }

}
