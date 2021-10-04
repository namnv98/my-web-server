package com.nnv.xml;

import java.util.ArrayList;
import java.util.List;

public class FilterXml {
    public String displayName;
    public Filter filter = new Filter();
    public FilterMapping filterMapping = new FilterMapping();

    public FilterXml.Param newParam(String paramName, String getParamValue) {
        return new FilterXml.Param(paramName, getParamValue);
    }


    public class Filter {
        private String filterName;
        private String filterClass;
        private List<FilterXml.Param> initParam = new ArrayList<>();

        public String getFilterName() {
            return filterName;
        }

        public void setFilterName(String filterName) {
            this.filterName = filterName;
        }

        public String getFilterClass() {
            return filterClass;
        }

        public void setFilterClass(String classClass) {
            this.filterClass = classClass;
        }

        public List<Param> getInitParam() {
            return initParam;
        }

        public void setInitParam(List<Param> initParam) {
            this.initParam = initParam;
        }
    }


    public class FilterMapping {
        private String filterName;
        private String urlPattern;
        private String url;

        public String getFilterName() {
            return filterName;
        }

        public void setFilterName(String filterName) {
            this.filterName = filterName;
        }

        public String getUrlPattern() {
            return urlPattern;
        }

        public void setUrlPattern(String urlPattern) {
            this.urlPattern = urlPattern;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public class Param {
        public Param(String paramName, String getParamValue) {
            this.paramName = paramName;
            this.paramValue = getParamValue;
        }

        private String paramName;
        private String paramValue;

        public String getParamName() {
            return paramName;
        }

        public void setParamName(String paramName) {
            this.paramName = paramName;
        }

        public String getParamValue() {
            return paramValue;
        }

        public void setParamValue(String paramValue) {
            this.paramValue = paramValue;
        }
    }

}
