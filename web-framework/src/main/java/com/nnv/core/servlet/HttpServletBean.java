package com.nnv.core.servlet;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.*;

public abstract class HttpServletBean extends HttpServlet {
    protected Map<String, Object> propertyValue = new HashMap<>();

    @Override
    public void init() throws ServletException {
        servletConfigPropertyValues(getServletConfig(), propertyValue);
        initServletBean();
    }

    protected void initServletBean() throws ServletException {
    }

    @Override
    public String getServletName() {
        return (getServletConfig() != null ? getServletConfig().getServletName() : null);
    }

    public void servletConfigPropertyValues(ServletConfig config, Map<String, Object> propertyValue) {
        Enumeration<String> paramNames = config.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            String property = paramNames.nextElement();
            Object value = config.getInitParameter(property);
            propertyValue.put(property, value);
        }
    }

}
