package com.nnv.core.context;

import javax.servlet.ServletContext;

public class WebApplicationContext extends ApplicationContext {

    public static final String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";
    public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";

    public ServletContext getServletContext() {
        return null;
    }
}
