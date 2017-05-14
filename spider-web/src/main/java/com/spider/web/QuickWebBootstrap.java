package com.spider.web;


import com.spider.common.utils.PropertieUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.util.ArrayList;

/**
 * springboot web启动类
 * Created by wangpeng on 2016/9/29.
 */
@Configuration
@ComponentScan("com.spider")
@EnableConfigurationProperties
@ImportResource({"classpath*:applicationContext-*.xml", "classpath*:spring/applicationContext-*.xml"})
@EnableAutoConfiguration
public class QuickWebBootstrap extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer{

    private static final Logger LOGGER = LoggerFactory.getLogger(QuickWebBootstrap.class);

    private static final String WEB_CONTEXT_PATH = "web.contextPath";
    private static final String WEB_PORT = "web.port";
    private static final String WEB_DOCUMENT_ROOT = "web.web.document.root";
    private static final String WEB_SCAN_PACKAGE = "web.scan.package";

    public static void main(String[] args) {
        ArrayList sourceList = new ArrayList();
        sourceList.add(QuickWebBootstrap.class);
        Object[] sources = sourceList.toArray(new Object[sourceList.size()]);
        SpringApplication springApplication = new SpringApplication(sources);
        springApplication.addInitializers(new ApplicationContextInitializer[]{new QuickWebBootstrap.WebApplicationContextInitializer()});
        springApplication.run(args);
    }


    @Override
    public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
        configurableEmbeddedServletContainer.setContextPath(PropertieUtils.getString(WEB_CONTEXT_PATH));
        configurableEmbeddedServletContainer.setPort(PropertieUtils.getInt(WEB_PORT));
        String documentRootPath = PropertieUtils.getString(WEB_DOCUMENT_ROOT);
        if(StringUtils.isNoneBlank(documentRootPath)){
            configurableEmbeddedServletContainer.setDocumentRoot(new File(documentRootPath));
        }
    }


    public static class WebApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public WebApplicationContextInitializer() {
        }

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            String scanPackage = PropertieUtils.getString(WEB_SCAN_PACKAGE);
            if(!org.springframework.util.StringUtils.isEmpty(scanPackage)) {
                BeanDefinitionRegistry beanDefinitionRegistry = null;
                if(configurableApplicationContext instanceof BeanDefinitionRegistry) {
                    beanDefinitionRegistry = (BeanDefinitionRegistry)configurableApplicationContext;
                } else {
                    if(!(configurableApplicationContext instanceof AbstractApplicationContext)) {
                        throw new IllegalStateException("===================Could not locate BeanDefinitionRegistry");
                    }

                    beanDefinitionRegistry = (BeanDefinitionRegistry)configurableApplicationContext.getBeanFactory();
                }

                String[] packages = org.springframework.util.StringUtils.tokenizeToStringArray(scanPackage, ",; \t\n");
                if(packages != null && packages.length > 0) {
                    ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);
                    int loadCount = scanner.scan(packages);
                    QuickWebBootstrap.LOGGER.info("===================Load bean from custom scan package:{},loadcount:{}", packages, Integer.valueOf(loadCount));
                }
            }

        }
    }
}

