package com.spider.core.client;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.protocol.HttpContext;

/**
 * Cookie
 */
public class CoreCookieSpecProvider implements CookieSpecProvider {
    public CookieSpec create(HttpContext context) {
        return new BrowserCompatSpec() {
            @Override
            public void validate(Cookie cookie, CookieOrigin origin)
                    throws MalformedCookieException {
                // Oh, I am easy
            }
        };
    }
}
