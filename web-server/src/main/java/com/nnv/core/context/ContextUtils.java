package com.nnv.core.context;

import com.nnv.xml.ContextParamXml;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContextUtils {
    static List<MyServletContext> contextList = new ArrayList<>();

    private static ContextUtils context;

    private ContextUtils(List<MyServletContext> contextList) {
        this.contextList = contextList;
    }

    public static ContextUtils getInstance() {
        if (context == null) {
            context = new ContextUtils(new ArrayList<>());
        }
        return context;
    }

    public void addServletContext(List<MyServletContext> contextList) {
        for (MyServletContext myServletContext : contextList) {
            addServletContext(myServletContext);
        }
    }

    public void addServletContext(MyServletContext servletContext) {
        contextList.add(servletContext);

        List<ContextParamXml> contextParamList = servletContext.getContext().getContextParamDefs();
        for (ContextParamXml contextParam : contextParamList) {
            servletContext.setInitParameter(contextParam.getParamName(), contextParam.getGetParamValue());
        }
        startListenerContext(servletContext);
    }

    private void startListenerContext(MyServletContext servletContext) {
        List<ServletContextListener> servletContextListeners = servletContext.getContext().getListenerContext();
        for (ServletContextListener servletContextListener : servletContextListeners) {
            ServletContextEvent servletContextEvent = new ServletContextEvent(servletContext);
            servletContextListener.contextInitialized(servletContextEvent);
        }
    }

    public MyServletContext getApplicationContext(String appName) {
        Optional<MyServletContext> context = contextList.stream().filter(a -> a.getContext().getAppName().equals(appName)).findAny();
        if (context.isPresent()) {
            return context.get();
        }
        return null;
    }
}
