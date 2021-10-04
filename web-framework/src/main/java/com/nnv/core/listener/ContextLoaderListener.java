package com.nnv.core.listener;

import com.nnv.core.context.WebApplicationContext;
import com.nnv.core.context.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextLoaderListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.initWebApplicationContext(sce.getServletContext());
        webApplicationContext.setDisplayName("Root Application Context");

        sce.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
