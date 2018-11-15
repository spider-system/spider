package com.spider.core.webmagic.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spider.common.utils.ToutiaoUtil;
import com.spider.core.webmagic.pipeline.ToutiaoAppPipeline;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

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
            String url = page.getUrl().get();
            String min_behot_time_before = page.getUrl().regex("min_behot_time=(.*?)&").get();
            String last_refresh_sub_entrance_interval_before = page.getUrl().regex("last_refresh_sub_entrance_interval=(.*?)&").get();
            String tt_from_before = page.getUrl().regex("tt_from=(.*?)&").get();
            url = url.replace("min_behot_time="+min_behot_time_before+"&","min_behot_time="+String.valueOf(min_behot_time)+"&")
                    .replace("last_refresh_sub_entrance_interval="+last_refresh_sub_entrance_interval_before+"&","last_refresh_sub_entrance_interval="+String.valueOf(time)+"&")
                    //.replace("loc_time=(.*?)&",String.valueOf(time))
                    .replace("tt_from="+tt_from_before+"&","tt_from="+more_tt_from_ios+"&")
                    .replace("list_count="+listCountString+"&","list_count="+String.valueOf(nextListCount)+"&");
            page.addTargetRequest(url);
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
    private static final String first_tt_from_ios = "enter_auto";
    private static final String more_tt_from = "pre_load_more";
    private static final String more_tt_from_ios = "load_more";
    private static final String pull_tt_from = "pull";
    private static final String last_read__tt_from = "last_read";

    private static final String ROOT_URL = "http://is.snssdk.com/api/news/feed/v88/";


    private static final String PARMA = "list_count=${list_count}&support_rn=4&refer=1&refresh_reason=5&session_refresh_idx=1&count=20&min_behot_time=${min_behot_time}&last_refresh_sub_entrance_interval=${last_refresh_sub_entrance_interval}&loc_mode=7&loc_time=${loc_time}&tt_from=${tt_from}&iid=50911165486&device_id=53247747507&ac=wifi&channel=oppo-cpa&aid=13&app_name=news_article&version_code=698&version_name=6.9.8&device_platform=android&ssmix=a&device_type=ONEPLUS+A6000&device_brand=OnePlus&language=zh&os_api=28&os_version=9&uuid=869897038475751&openudid=8954f426e1c4b04c&manifest_version_code=698&resolution=1080*2200&dpi=380&update_version_code=69811&_rticket=1542110861223&plugin=10566&pos=5r_-9Onkv6e_ewAweCoDeCUfv7G_8fLz-vTp6Pn4v6esr62zra6uqqqvsb_x_On06ej5-L-nrq2zr6Srrq2rsb_88Pzt3vTp5L-nv3sAMHgqA3glH7-xv_zw_O3R8vP69Ono-fi_p6yvrbOtrqWuqaSxv_zw_O3R_On06ej5-L-nrq2zr6SupK2l4A%3D%3D&fp=HrT_F2mucSZWFlwIP2U1FYmeFrxO&rom_version=28&ts=1542110861&as=a215abfe2db82b1efa4355&mas=007eb510d49de96740a75f8f7f5a1f380b6604e84c06686ea4&cp=5eb4eba7b6e8dq1";
    private static final String PARMA_IOS = "fp=PSTqFSctFWmWFlHOFlU1FlxeFSPr&version_code=6.9.2&tma_jssdk_version=1.2.2.0&app_name=news_article&vid=CF62029C-8C5A-4F73-8077-2E3D2F5B2A32&device_id=35171868544&channel=App%20Store&resolution=2048*1536&aid=13&ab_feature=z1&ab_version=574251,580098,570597,587869,486953,548453,577228,581254,586030,571130,566629,581493,239097,568567,588258,493250,571697,374117,585115,588069,576161,569578,550042,435213,586996,569344,573916,578707,377093,584496,522765,385744,416055,592541,576969,558139,555255,378451,579057,579730,582988,574603,587788,567866,585657,326532,591615,586291,574050,590579,585714,576647,554836,549647,424177,583593,31211,572465,582641,591175,442255,542177,589415,591008,590524,569779,582114,546701,281295,589817,592704,581398,325611,578586,590693,592393,576541,568792,586300,498375,592616,580578,467515,444465,584240,579901,580448,590267,589102,457480&openudid=fe9f5c81fef9bcb36f181390c41f844cd54df09c&update_version_code=69212&idfv=CF62029C-8C5A-4F73-8077-2E3D2F5B2A32&ac=WIFI&os_version=12.0.1&ssmix=a&device_platform=ipad&iid=50681896452&ab_client=a1,f2,f7,e1&device_type=iPad%20Mini%20Retina&idfa=307A7671-AECB-4945-A1CE-95CCFFDDE7CC&detail=1&refresh_reason=5&last_refresh_sub_entrance_interval=${last_refresh_sub_entrance_interval}&tt_from=${tt_from}&count=20&list_count=${list_count}&support_rn=4&LBS_status=authroize&cp=50BfEeA7Dd982q1&loc_mode=1&min_behot_time=${min_behot_time}&image=1&session_refresh_idx=3&strict=0&refer=1&city=&concern_id=6286225228934679042&language=zh-Hans-CN&as=a215fdfe82c81b19ea8079&ts=1542117762";

    public static void main(String[] args) {
        String paramStr = "{\"LBS_status\":\"deny\",\"ab_client\":\"a1,f2,f7,e1\",\"ab_feature\":\"z1\",\"ab_version\":\"574248,580098,570602,587869,486953,548454,577228,586023,571130,591886,239096,568569,170988,493250,571684,374119,588069,581761,576062,569578,562844,550042,435213,320832,586994,569343,545895,405355,578707,521962,584498,522765,416055,592541,558140,555254,378451,471407,579057,593074,582987,574603,271178,587785,585657,326532,591615,586291,573284,594159,583111,589794,593483,591900,587314,469022,554836,549647,424176,583593,31210,572465,583280,590232,591177,442255,593643,589412,584528,590522,569778,582114,546700,280449,281295,589814,473328,581398,325616,578587,590694,586519,511255,568792,498375,580578,467513,593904,252783,566292,444464,584240,579905,580448,590265,589102,586956,590514,572567,457481,562442\",\"ac\":\"WIFI\",\"aid\":\"13\",\"app_name\":\"news_article\",\"channel\":\"App Store\",\"city\":\"\",\"concern_id\":\"6286225228934679042\",\"count\":\"20\",\"detail\":\"1\",\"device_id\":\"59029797261\",\"device_platform\":\"iphone\",\"device_type\":\"iPhone 7 Plus\",\"fp\":\"crT_P2mucWLMFlTZP2U1F2KIFzKe\",\"idfa\":\"C2C21E1A-60E3-4E00-884B-71E72432074F\",\"idfv\":\"D9C014A4-65BE-4ECD-90A6-F162DAE0FB06\",\"iid\":\"50103135774\",\"image\":\"1\",\"language\":\"zh-Hans-CN\",\"last_refresh_sub_entrance_interval\":\"${last_refresh_sub_entrance_interval}\",\"list_count\":\"${list_count}\",\"loc_mode\":\"0\",\"min_behot_time\":\"${min_behot_time}\",\"openudid\":\"c5b86e8e398cee66cdb09acee7ab2d7fd161970a\",\"os_version\":\"12.1\",\"refer\":\"1\",\"refresh_reason\":\"0\",\"resolution\":\"1242*2208\",\"session_refresh_idx\":\"4\",\"ssmix\":\"a\",\"st_time\":\"56\",\"strict\":\"0\",\"tma_jssdk_version\":\"1.3.0.3\",\"ts\":\"1542179121\",\"tt_from\":\"${tt_from}\",\"update_version_code\":\"69722\",\"version_code\":\"6.9.7\",\"vid\":\"D9C014A4-65BE-4ECD-90A6-F162DAE0FB06\"}";
        Map<String,String> paramMap = JSON.parseObject(paramStr,Map.class);
        String params = ToutiaoUtil.contactUrlParams(paramMap);
        Long time = System.currentTimeMillis();
        Long min_behot_time = time - 1000*60*60;
        params = params.replace("${min_behot_time}",String.valueOf(min_behot_time))
                .replace("${last_refresh_sub_entrance_interval}",String.valueOf(time))
                .replace("${loc_time}",String.valueOf(time))
                .replace("${tt_from}",first_tt_from_ios)
                .replace("${list_count}","1");
        Spider.create(new ToutiaoAppPageProcessor())
                .addUrl(ROOT_URL+"?"+params)
                .addPipeline(new ToutiaoAppPipeline("/Users/wangpeng/Documents/webmagic-crawler-file"))
                .run();
    }
}
