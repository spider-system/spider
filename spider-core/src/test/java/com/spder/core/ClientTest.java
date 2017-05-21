package com.spder.core;

import com.spider.core.client.Client;
import org.junit.Test;

/**
 * Created by wangpeng on 2017/5/14.
 */
public class ClientTest {


    @Test
    public void testCrawler(){
        String url = "https://github.com/spider-system/spider";
        Client client = Client.newInstance();
        client.get(url);
        String result = client.getData();
        System.out.println(result);

    }

}
