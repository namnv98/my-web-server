package com.nnv.xml;

import java.util.List;

public class WebXml {
    private String path;
    private String appName;
    private List<ListenerXml> listener;
    private List<ServletXml> servlet;
    private List<FilterXml> filter;
    private List<ContextParamXml> contextParam;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAppName() {
        return path.substring(path.lastIndexOf("\\") + 1);
    }

    public List<ListenerXml> getListener() {
        return listener;
    }

    public void setListener(List<ListenerXml> listener) {
        this.listener = listener;
    }

    public List<ServletXml> getServlet() {
        return servlet;
    }

    public void setServlet(List<ServletXml> servlet) {
        this.servlet = servlet;
    }

    public List<FilterXml> getFilter() {
        return filter;
    }

    public void setFilter(List<FilterXml> filter) {
        this.filter = filter;
    }

    public List<ContextParamXml> getContextParam() {
        return contextParam;
    }

    public void setContextParam(List<ContextParamXml> contextParam) {
        this.contextParam = contextParam;
    }
}
