package com.nnv.core.context.servlet;

import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StandardWrapper {
    private String url;
    private String servletName;
    private volatile Servlet instance;
    private HashMap<String, String> parameters = new HashMap<>();
    private String servletClass;
    private boolean overridable;
    private boolean isInit = true;

    public Servlet getInstance() {
        return instance;
    }

    public boolean checkMatchUrl(String requestUrl, String appName) {
        String urlPattern = replaceUrl(url, appName);
        return match(urlPattern, requestUrl);
    }

    boolean match(String pattern, String value) {
        Matcher matcher = Pattern.compile(pattern).matcher(value);
        boolean matchFound = matcher.find();
        if (matchFound) {
            return true;
        } else {
            return false;
        }
    }

    private String replaceUrl(String url, String appName) {
        return String.format("/%s", appName) + url.replace("*", "([A-z-0-9])\\w+");
    }

    public void addInitParameter(String name, String value) {
        parameters.put(name, value);
    }

    public String getServletName() {
        return servletName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public void setServlet(Servlet servlet) {
        this.instance = servlet;
    }

    public void setInstance(Servlet instance) {
        this.instance = instance;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getServletClass() {
        return servletClass;
    }

    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }

    public boolean isOverridable() {
        return overridable;
    }

    public void setOverridable(boolean overridable) {
        this.overridable = overridable;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }
}
