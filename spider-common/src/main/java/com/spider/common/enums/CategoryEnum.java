package com.spider.common.enums;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * Created by wangpeng on 2017/5/21.
 */
public enum  CategoryEnum {

    /*'推荐': '__all__',
        '热点': 'news_hot',
        '社会': 'news_society',
        '娱乐': 'news_entertainment',
        '科技': 'news_tech',
        '军事': 'news_military',
        '体育': 'news_sports'
        '汽车': 'news_car',
        '财经': 'news_finance',
        '国际': 'news_world',
        '时尚': 'news_fashion',
        '旅游': 'news_travel',
        '探索': 'news_discovery',
        '育儿': 'news_baby',
        '养生': 'news_regimen',
        '故事': 'news_story',
        '美文': 'news_essay',
        '游戏': 'news_game',
        '历史': 'news_history',
        '美食': 'news_food',*/
    NEWS_HOT("news_hot","热点"),
    NEWS_IMAGE("news_image","图片"),
    ESSAY_JOKE("essay_joke","段子"),
    NEWS_SOCIETY("news_society","社会"),
    NEWS_ENTERTAINMENT("news_entertainment","娱乐"),
    NEWS_TECH("news_tech","科技"),
    NEWS_SPORTS("news_sports","体育"),
    NEWS_CAR("news_car","汽车"),
    NEWS_FINANCE("news_finance","汽车"),
    FUNNY("funny","搞笑"),
    ;
    private String code;
    private String message;

    CategoryEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static List<String> getCategoryList(){
        List<String> list = Lists.newArrayList();
        for(CategoryEnum categoryEnum : values()){
            list.add(categoryEnum.getCode());
        }
        return list;
    }

}
