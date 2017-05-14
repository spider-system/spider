package com.spider.web.configration;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spider.common.constants.GlobConts;
import com.spider.web.configration.advice.LogViewResponseBodyAdvice;
import com.spider.web.intercept.SystemInterceptor;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Created by wangpeng on 2017/5/14.
 */
public class SpringMvcConfigraion extends WebMvcConfigurationSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringMvcConfigraion.class);

    private static final boolean jackson2Present = ClassUtils.isPresent(
        "com.fasterxml.jackson.databind.ObjectMapper",
        WebMvcConfigurationSupport.class.getClassLoader())
        && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator",
        WebMvcConfigurationSupport.class.getClassLoader());

//    @Bean
//    public ViewResolver viewResolver() {
//        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//        resolver.setPrefix("/static/views/");
//        resolver.setSuffix(".html");
//        resolver.setExposeContextBeansAsAttributes(true);
//        return resolver;
//    }


    public List<HttpMessageConverter<?>> setMessageConverters(){
        List<HttpMessageConverter<?>> messageConverters = Lists.newArrayList();
        messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        Gson gson = new GsonBuilder().setDateFormat(GlobConts.DEFAULT_FORMATTER_YYYYMMDDHHMMSS).create();
        gsonHttpMessageConverter.setGson(gson);
        messageConverters.add(gsonHttpMessageConverter);
        return messageConverters;
    }


    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        List<HandlerMethodArgumentResolver> argumentResolvers = Lists.newArrayList();
        addArgumentResolvers(argumentResolvers);

        List<HandlerMethodReturnValueHandler> returnValueHandlers = Lists.newArrayList();
        addReturnValueHandlers(returnValueHandlers);

        RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
        adapter.setContentNegotiationManager(mvcContentNegotiationManager());
        adapter.setMessageConverters(setMessageConverters());
        adapter.setWebBindingInitializer(getConfigurableWebBindingInitializer());
        adapter.setCustomArgumentResolvers(argumentResolvers);
        adapter.setCustomReturnValueHandlers(returnValueHandlers);

        if (jackson2Present) {
            List<ResponseBodyAdvice<?>> interceptors = Lists.newArrayList();
            interceptors.add(new JsonViewResponseBodyAdvice());
            interceptors.add(new LogViewResponseBodyAdvice());
            adapter.setResponseBodyAdvice(interceptors);
        }

        SpiderAsyncSupportConfigurer configurer = new SpiderAsyncSupportConfigurer();
        configureAsyncSupport(configurer);

        if (configurer.getTaskExecutor() != null) {
            adapter.setTaskExecutor(configurer.getTaskExecutor());
        }
        if (configurer.getTimeout() != null) {
            adapter.setAsyncRequestTimeout(configurer.getTimeout());
        }
        adapter.setCallableInterceptors(configurer.getCallableInterceptors());
        adapter.setDeferredResultInterceptors(configurer
            .getDeferredResultInterceptors());

        return adapter;
    }


    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor(){
        LOGGER.info("LocaleChangeInterceptor");
        return new LocaleChangeInterceptor();
    }

    @Bean
    public SystemInterceptor initializingInterceptor(){
        return new SystemInterceptor();
    }


    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(initializingInterceptor());
    }


//    /**
//     * 静态资源配置
//     * @param registry
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("/WEB-INF/static/");
//    }

}
