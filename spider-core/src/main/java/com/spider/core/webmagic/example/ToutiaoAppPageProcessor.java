package com.spider.core.webmagic.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spider.core.webmagic.Page;
import com.spider.core.webmagic.Site;
import com.spider.core.webmagic.Spider;
import com.spider.core.webmagic.pipeline.ToutiaoAppPipeline;
import com.spider.core.webmagic.processor.PageProcessor;

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
            Long min_behot_time = time - 1000*60*60*24;
            String params = PARMA.replace("${min_behot_time}",String.valueOf(min_behot_time))
                    .replace("${last_refresh_sub_entrance_interval}",String.valueOf(time))
                    .replace("${loc_time}",String.valueOf(time))
                    .replace("${tt_from}",moew_tt_from)
                    .replace("${list_count}",String.valueOf(nextListCount));
            page.addTargetRequest(ROOT_URL+"?"+params);
        }else {
            page.setSkip(true);
        }
        page.putField("data",jsonObject.getJSONArray("data"));
    }

    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(3).setTimeOut(10000).setSleepTime(1000);
    }

    private static final String first_tt_from = "enter_auto";
    private static final String moew_tt_from = "pre_load_more";
    private static final String pull_tt_from = "pull";

    private static final String ROOT_URL = "http://lf.snssdk.com/api/news/feed/v88/";

    private static final String PARMA = "list_count=${list_count}&support_rn=4&refer=1&refresh_reason=5&session_refresh_idx=1&count=20&min_behot_time=${min_behot_time}&last_refresh_sub_entrance_interval=${last_refresh_sub_entrance_interval}&loc_mode=7&loc_time=${loc_time}&tt_from=${tt_from}&iid=50911165486&device_id=53247747507&ac=wifi&channel=oppo-cpa&aid=13&app_name=news_article&version_code=698&version_name=6.9.8&device_platform=android&ssmix=a&device_type=ONEPLUS+A6000&device_brand=OnePlus&language=zh&os_api=28&os_version=9&uuid=869897038475751&openudid=8954f426e1c4b04c&manifest_version_code=698&resolution=1080*2200&dpi=380&update_version_code=69811&_rticket=1542110861223&plugin=10566&pos=5r_-9Onkv6e_ewAweCoDeCUfv7G_8fLz-vTp6Pn4v6esr62zra6uqqqvsb_x_On06ej5-L-nrq2zr6Srrq2rsb_88Pzt3vTp5L-nv3sAMHgqA3glH7-xv_zw_O3R8vP69Ono-fi_p6yvrbOtrqWuqaSxv_zw_O3R_On06ej5-L-nrq2zr6SupK2l4A%3D%3D&fp=HrT_F2mucSZWFlwIP2U1FYmeFrxO&rom_version=28&ts=1542110861&as=a215abfe2db82b1efa4355&mas=007eb510d49de96740a75f8f7f5a1f380b6604e84c06686ea4&cp=5eb4eba7b6e8dq1";


    public static void main(String[] args) {
        Long time = System.currentTimeMillis();
        Long min_behot_time = time - 1000*60*60*24;
        String params = PARMA.replace("${min_behot_time}",String.valueOf(min_behot_time))
                .replace("${last_refresh_sub_entrance_interval}",String.valueOf(time))
                .replace("${loc_time}",String.valueOf(time))
                .replace("${tt_from}",first_tt_from)
                .replace("${list_count}","1");
        //String url = "http://lf.snssdk.com/api/news/feed/v88/?list_count=14&support_rn=4&refer=1&refresh_reason=5&session_refresh_idx=1&count=20&min_behot_time="+min_behot_time+"&last_refresh_sub_entrance_interval="+time+"&loc_mode=7&loc_time="+time+"&latitude=30.296306&longitude=120.033772&city=杭州&tt_from=enter_auto&lac=22709&cid=153598081&plugin_enable=3&st_time=0&strict=1&iid=50911165486&device_id=53247747507&ac=wifi&channel=oppo-cpa&aid=13&app_name=news_article&version_code=698&version_name=6.9.8&device_platform=android&ab_version=574248%2C580098%2C587869%2C486951%2C581259%2C586023%2C571131%2C547148%2C564265%2C581491%2C239098%2C568567%2C588253%2C170988%2C493250%2C571681%2C374117%2C585071%2C588069%2C581759%2C581916%2C569578%2C562846%2C550042%2C435214%2C592337%2C572494%2C586995%2C545899%2C573919%2C578707%2C584497%2C522765%2C579227%2C416055%2C592541%2C392460%2C470729%2C558139%2C586261%2C555254%2C378450%2C471406%2C579057%2C579727%2C582988%2C574603%2C271178%2C587786%2C585658%2C326532%2C591615%2C586291%2C556781%2C589837%2C589792%2C584940%2C591898%2C587316%2C589624%2C576647%2C554836%2C549647%2C424176%2C583593%2C572465%2C588171%2C591176%2C442255%2C542179%2C589417%2C579061%2C590524%2C582114%2C546703%2C280448%2C281298%2C589815%2C592704%2C581398%2C325611%2C578586%2C476047%2C533647%2C592803%2C568793%2C586300%2C431135%2C498375%2C580578%2C467514%2C583598%2C444465%2C584241%2C579910%2C580448%2C590261%2C562442%2C589141%2C589102%2C553953%2C457481&ab_client=a1%2Cc4%2Ce1%2Cf1%2Cg2%2Cf7&ab_group=100170%2C94563%2C102749%2C181429&ab_feature=94563%2C102749&abflag=3&ssmix=a&device_type=ONEPLUS+A6000&device_brand=OnePlus&language=zh&os_api=28&os_version=9&uuid=869897038475751&openudid=8954f426e1c4b04c&manifest_version_code=698&resolution=1080*2200&dpi=380&update_version_code=69811&_rticket=1542110861223&plugin=10566&pos=5r_-9Onkv6e_ewAweCoDeCUfv7G_8fLz-vTp6Pn4v6esr62zra6uqqqvsb_x_On06ej5-L-nrq2zr6Srrq2rsb_88Pzt3vTp5L-nv3sAMHgqA3glH7-xv_zw_O3R8vP69Ono-fi_p6yvrbOtrqWuqaSxv_zw_O3R_On06ej5-L-nrq2zr6SupK2l4A%3D%3D&fp=HrT_F2mucSZWFlwIP2U1FYmeFrxO&rom_version=28&ts=1542110861&as=a215abfe2db82b1efa4355&mas=007eb510d49de96740a75f8f7f5a1f380b6604e84c06686ea4&cp=5eb4eba7b6e8dq1";
        Spider.create(new ToutiaoAppPageProcessor())
                .addUrl(ROOT_URL+"?"+params)
                .addPipeline(new ToutiaoAppPipeline("/Users/wangpeng/Documents/webmagic-crawler-file"))
                //.addPipeline(new TouTiaoPipeline("/Users/wangpeng/Documents/webmagic-crawler-file"))
                .run();
    }
}
