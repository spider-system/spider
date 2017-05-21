package com.spider.common.base;


import com.spider.common.constants.GlobConts;

/**
 * Created by wonpera on 2017/2/18.
 */
public class WebPageParameter extends SessionParameter {


    private Integer requestOffset;
    private Integer requestCount;
    private Integer pageSize = GlobConts.DEFUALT_PAGE_SIZE;
    private Integer page=1;

    public Integer getRequestOffset() {
        return requestOffset;
    }

    public void setRequestOffset(Integer requestOffset) {
        this.requestOffset = requestOffset;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
