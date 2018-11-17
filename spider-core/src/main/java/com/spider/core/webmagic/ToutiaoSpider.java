package com.spider.core.webmagic;

import com.spider.common.constants.GlobConts;
import com.spider.core.webmagic.handler.StatusListener;
import com.spider.core.webmagic.monitor.SpiderMonitor;
import com.spider.core.webmagic.monitor.SpiderStatus;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author: wangpeng
 * @date: 2018/11/17 18:51
 */
public class ToutiaoSpider extends Spider {


    private StatusListener statusListener;

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
        if(statusListener == null){
            statusListener = new StatusListener() {
                @Override
                public void reportSoptEvent(String task) {
                    //do nothing
                }

                @Override
                public void reportStartEvent(String task) {
                    //do nothing
                }

                @Override
                public void restartTask(String task) {
                    //do nothing
                }
            };
        }
        statusListener.reportStartEvent(getUUID());
    }

    public void beforeDestory(){
        SpiderStatus spiderStatus = SpiderMonitor.instance().getSpiderStatusByUUID(getUUID());
        if(!spiderStatus.getManualStop()){
            SpiderMonitor.instance().unRegisterSpdierByTask(getUUID());
        }
        if(spiderStatus != null){
            Long startTime = spiderStatus.getStartTime().getTime();
            Long runningTime = System.currentTimeMillis()-startTime;
            //跑的时间太短 重启任务
            if(!spiderStatus.getManualStop() && runningTime < GlobConts.CRAWLER_TIME_TOO_SHORT){
                statusListener.restartTask(getUUID());
            }else {
                statusListener.reportSoptEvent(getUUID());
            }
        }
    }

    public ToutiaoSpider setStatusListener(StatusListener statusListener){
        this.statusListener = statusListener;
        return this;
    }

}
