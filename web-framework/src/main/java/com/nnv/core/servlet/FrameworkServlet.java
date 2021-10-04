package com.nnv.core.servlet;

import com.nnv.core.context.ApplicationContext;
import com.nnv.core.context.WebApplicationContext;
import com.nnv.core.context.WebApplicationContextUtils;
import com.nnv.core.context.support.XmlWebApplicationContext;

import javax.servlet.ServletException;

public abstract class FrameworkServlet extends HttpServletBean {
    public static final String DEFAULT_NAMESPACE_SUFFIX = "-servlet";

    public static final String SERVLET_CONTEXT_PREFIX = FrameworkServlet.class.getName() + ".CONTEXT.";
    private static final String INIT_PARAM_DELIMITERS = ",; \t\n";
    public static final Class<?> DEFAULT_CONTEXT_CLASS = XmlWebApplicationContext.class;

    private Class<?> contextClass = DEFAULT_CONTEXT_CLASS;
    private String contextId;
    private String namespace;

    private String contextAttribute;
    private String contextConfigLocation;
    private String contextInitializerClasses;
    private WebApplicationContext webApplicationContext;

    @Override
    protected void initServletBean() throws ServletException {
        WebApplicationContext rootContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        String path = (String) propertyValue.get("contextConfigLocation");

        WebApplicationContext wac = WebApplicationContextUtils.initWebApplicationContext(SERVLET_CONTEXT_PREFIX + getNamespace(), getServletContext(), path);

        if (wac != null && rootContext != null) {
            wac.setParent(rootContext);
        }
        this.webApplicationContext = wac;

        onRefresh(wac);
    }

    protected void onRefresh(ApplicationContext context) {

    }

    public String getContextAttribute() {
        return this.contextAttribute;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return (this.namespace != null ? this.namespace : getServletName() + DEFAULT_NAMESPACE_SUFFIX);
    }

    public WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }

    public void setWebApplicationContext(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }
}
