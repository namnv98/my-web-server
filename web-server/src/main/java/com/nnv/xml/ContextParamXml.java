package com.nnv.xml;

public class ContextParamXml {
    public ContextParamXml(String paramName, String getParamValue) {
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
