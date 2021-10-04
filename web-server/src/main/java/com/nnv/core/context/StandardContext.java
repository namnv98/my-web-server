package com.nnv.core.context;

import com.nnv.core.context.filter.FilterDef;
import com.nnv.core.context.servlet.StandardWrapper;
import com.nnv.xml.ContextParamXml;

import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StandardContext {
    private String appName;
    private String urlPattern;
    private String docBase;
    private String originalDocBase;
    private String workDir;
    private HashMap<String, FilterDef> filterDefs = new HashMap<>();
    private HashMap<String, StandardWrapper> servletDefs = new HashMap<>();
    private HashMap<String, ContextParamXml> contextParamDefs = new HashMap<>();
    private HashMap<String, ServletContextListener> listenerContext = new HashMap<>();

    public void setContextParamDefs(HashMap<String, ContextParamXml> contextParamDefs) {
        this.contextParamDefs = contextParamDefs;
    }

    public List<ServletContextListener> getListenerContext() {
        return new ArrayList<>(listenerContext.values());
    }

    public void setListenerContext(String k, ServletContextListener v) {
        this.listenerContext.put(k, v);
    }

    public List<ContextParamXml> getContextParamDefs() {
        return new ArrayList<>(contextParamDefs.values());
    }

    public void setContextParamDefs(String key, ContextParamXml contextParamDefs) {
        this.contextParamDefs.put(key, contextParamDefs);
    }

    public void addChild(StandardWrapper standardWrapper) {
        servletDefs.put(standardWrapper.getServletName(), standardWrapper);
    }

    public void addFilterDef(FilterDef filterDef) {
        filterDefs.put(filterDef.getFilterName(), filterDef);
    }

    public FilterDef findFilterDef(String name) {
        return filterDefs.get(name);
    }

    public StandardWrapper findChild(String name) {
        return servletDefs.get(name);
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDocBase() {
        return docBase;
    }

    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    public String getOriginalDocBase() {
        return originalDocBase;
    }

    public void setOriginalDocBase(String originalDocBase) {
        this.originalDocBase = originalDocBase;
    }

    public String getWorkDir() {
        return workDir;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public HashMap<String, FilterDef> getFilterDefs() {
        return filterDefs;
    }

    public ArrayList<FilterDef> getFilterDefsList() {
        return new ArrayList<FilterDef>(filterDefs.values());
    }

    public void setFilterDefs(HashMap<String, FilterDef> filterDefs) {
        this.filterDefs = filterDefs;
    }

    public HashMap<String, StandardWrapper> getServletDefs() {
        return servletDefs;
    }

    public ArrayList<StandardWrapper> getServletDefsList() {
        return new ArrayList<StandardWrapper>(servletDefs.values());
    }

    public void setServletDefs(HashMap<String, StandardWrapper> servletDefs) {
        this.servletDefs = servletDefs;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }
}
