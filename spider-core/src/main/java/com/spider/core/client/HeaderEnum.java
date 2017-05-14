package com.spider.core.client;


public enum HeaderEnum {
    Accept("Accept"),
    Accept_Encoding("Accept-Encoding"),
    Accept_Language("Accept-Language"),
    Connection("Connection"),
    Content_Type("Content-Type"),
    Cookie("Cookie"),
    Host("Host"),
    Referer("Referer"),
    User_Agent("User-Agent"),
    ;

    private String value;

    HeaderEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
