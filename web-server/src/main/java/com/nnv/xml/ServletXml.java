package com.nnv.xml;

import java.util.ArrayList;
import java.util.List;

public class ServletXml {
    public String displayName;
    public Servlet servlet = new Servlet();
    public ServletMapping servletMapping = new ServletMapping();

    public Param newParam(String paramName, String getParamValue) {
        return new Param(paramName, getParamValue);
    }

    public class Servlet {
        private String servletName;
        private String servletClass;
        private List<Param> initParam = new ArrayList<>();

        public String getServletName() {
            return servletName;
        }

        public void setServletName(String servletName) {
            this.servletName = servletName;
        }

        public String getServletClass() {
            return servletClass;
        }

        public void setServletClass(String servletClass) {
            this.servletClass = servletClass;
        }

        public List<Param> getInitParam() {
            return initParam;
        }

        public void setInitParam(List<Param> initParam) {
            this.initParam = initParam;
        }
    }

    public class Param {
        public Param(String paramName, String getParamValue) {
            this.paramName = paramName;
            this.getParamValue = getParamValue;
        }

        private String paramName;
        private String getParamValue;

        public String getParamName() {
            return paramName;
        }

        public void setParamName(String paramName) {
            this.paramName = paramName;
        }

        public String getGetParamValue() {
            return getParamValue;
        }

        public void setGetParamValue(String getParamValue) {
            this.getParamValue = getParamValue;
        }
    }

    public class ServletMapping {
        private String servletName;
        private String urlPattern;
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getServletName() {
            return servletName;
        }

        public void setServletName(String servletName) {
            this.servletName = servletName;
        }

        public String getUrlPattern() {
            return urlPattern;
        }

        public void setUrlPattern(String urlPattern) {
            this.urlPattern = urlPattern;
        }
    }
}
