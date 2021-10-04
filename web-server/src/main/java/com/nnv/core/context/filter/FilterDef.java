package com.nnv.core.context.filter;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

public class FilterDef {
    private transient Filter filter;
    private String filterClass;
    private String filterName;
    private boolean isInit = true;
    private Map<String, String> parameters = new HashMap<>();

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(String filterClass) {
        this.filterClass = filterClass;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }
}
