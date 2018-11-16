package com.spider.core.webmagic.proxy;


import com.spider.core.webmagic.proxy.entity.Proxy;
import com.spider.core.webmagic.proxy.parser.Parser;

import java.util.List;

public interface ProxyListPageParser extends Parser {
    /**
     * 是否只要匿名代理
     */
    static final boolean anonymousFlag = true;
    List<Proxy> parse(String content);
}
