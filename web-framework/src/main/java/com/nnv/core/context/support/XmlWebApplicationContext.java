package com.nnv.core.context.support;

import com.nnv.core.beans.BeanFactory;
import com.nnv.core.context.WebApplicationContext;

import javax.servlet.ServletContext;

public class XmlWebApplicationContext extends WebApplicationContext {

    ServletContext servletContext;

    public XmlWebApplicationContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void loadBeanDefinitions(String path) {
        if (path == null) {
            path = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
        }
        setBeanFactory(new BeanFactory(path));
    }

    public static WebApplicationContext findWebApplicationContext(ServletContext servletContext) {
        return (WebApplicationContext) servletContext.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
