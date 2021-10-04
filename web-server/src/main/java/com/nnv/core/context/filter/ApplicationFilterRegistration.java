package com.nnv.core.context.filter;

import javax.servlet.*;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class ApplicationFilterRegistration implements FilterRegistration.Dynamic {
    final FilterDef filterDef;

    public ApplicationFilterRegistration(FilterDef standardWrapper) {
        this.filterDef = standardWrapper;
    }


    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {

    }

    @Override
    public Collection<String> getServletNameMappings() {
        return null;
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {

    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        return null;
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return false;
    }

    @Override
    public String getInitParameter(String name) {
        return null;
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        filterDef.setParameters(initParameters);
        return filterDef.getParameters().keySet();
    }

    @Override
    public Map<String, String> getInitParameters() {
        return filterDef.getParameters();
    }
}
