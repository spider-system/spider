package com.spider.core.webmagic.proxy.parser;


import com.spider.core.webmagic.proxy.entity.Page;

import java.util.List;

public interface ListPageParser extends Parser {
    List parseListPage(Page page);
}
