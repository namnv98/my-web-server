package com.nnv.core.context;

import com.nnv.core.context.support.XmlWebApplicationContext;

import javax.servlet.ServletContext;
import java.util.Enumeration;

public class WebApplicationContextUtils {

    public static WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
        XmlWebApplicationContext context = new XmlWebApplicationContext(servletContext);
        context.loadBeanDefinitions(null);
        return context;
    }

    public static WebApplicationContext initWebApplicationContext(String attribute, ServletContext servletContext, String path) {
        XmlWebApplicationContext context = new XmlWebApplicationContext(servletContext);
        context.loadBeanDefinitions(path);
        return context;
    }

    public static WebApplicationContext getRequiredWebApplicationContext(ServletContext sc) throws IllegalStateException {
        WebApplicationContext wac = getWebApplicationContext(sc);
        if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
        }
        return wac;
    }

    public static WebApplicationContext getWebApplicationContext(ServletContext sc) {
        return getWebApplicationContext(sc, WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }

    public static WebApplicationContext getWebApplicationContext(ServletContext sc, String attrName) {
        Object attr = sc.getAttribute(attrName);
        if (attr == null) {
            return null;
        }
        if (!(attr instanceof WebApplicationContext)) {
            throw new IllegalStateException("Context attribute is not of type WebApplicationContext: " + attr);
        }
        return (WebApplicationContext) attr;
    }

    public static WebApplicationContext findWebApplicationContext(ServletContext sc) {
        WebApplicationContext wac = getWebApplicationContext(sc);
        if (wac == null) {
            Enumeration<String> attrNames = sc.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String attrName = attrNames.nextElement();
                Object attrValue = sc.getAttribute(attrName);
                if (attrValue instanceof WebApplicationContext) {
                    if (wac != null) {
                        throw new IllegalStateException("No unique WebApplicationContext found: more than one " + "DispatcherServlet registered with publishContext=true?");
                    }
                    wac = (WebApplicationContext) attrValue;
                }
            }
        }
        return wac;
    }
}
