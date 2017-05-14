package com.spider.web.configration.adapter;

import java.util.List;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

/**
 * Created by wonpera on 2016/9/29.
 */
public class SpiderRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {
    /**
     * Template method to create a new InitBinderDataBinderFactory instance.
     * <p>
     * The default implementation creates a ServletRequestDataBinderFactory.
     * This can be overridden for custom ServletRequestDataBinder subclasses.
     *
     * @param binderMethods
     *            {@code @InitBinder} methods
     * @return the InitBinderDataBinderFactory instance to use
     * @throws Exception
     *             in case of invalid state or arguments
     */
    @Override
    protected InitBinderDataBinderFactory createDataBinderFactory(
            List<InvocableHandlerMethod> binderMethods) throws Exception {
        return new HanfuServletRequestDataBinderFactory(binderMethods,
                getWebBindingInitializer());
    }

    private class HanfuServletRequestDataBinderFactory extends
        ServletRequestDataBinderFactory {
        public HanfuServletRequestDataBinderFactory(
                List<InvocableHandlerMethod> binderMethods,
                WebBindingInitializer initializer) {
            super(binderMethods, initializer);
        }

        /**
         * Returns an instance of {@link ExtendedServletRequestDataBinder}.
         */
        @Override
        protected ServletRequestDataBinder createBinderInstance(Object target,
                                                                String objectName, NativeWebRequest request) {
            request.setAttribute("request_body_object", target,
                    RequestAttributes.SCOPE_REQUEST);
            return new ExtendedServletRequestDataBinder(target, objectName);
        }

    }
}
