package com.nnv.core.context;

import com.nnv.core.context.filter.ApplicationFilterRegistration;
import com.nnv.core.context.filter.FilterDef;
import com.nnv.core.context.servlet.ApplicationServletRegistration;
import com.nnv.core.context.servlet.StandardWrapper;

import javax.servlet.*;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MyServletContext implements ServletContext {
    protected Map<String, Object> attributes = new ConcurrentHashMap<>();
    private final StandardContext context;
    private final Map<String, String> parameters = new ConcurrentHashMap<>();

    public MyServletContext(StandardContext context) {
        this.context = context;
    }

    public StandardContext getContext() {
        return context;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public javax.servlet.ServletContext getContext(String uripath) {
        return null;
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    @Override
    public String getMimeType(String file) {
        return null;
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        return null;
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        return inputStream;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        return null;
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        return null;
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        return null;
    }

    @Override
    public Enumeration<String> getServletNames() {
        return null;
    }

    @Override
    public void log(String msg) {

    }

    @Override
    public void log(Exception exception, String msg) {

    }

    @Override
    public void log(String message, Throwable throwable) {

    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public String getServerInfo() {
        return null;
    }

    @Override
    public String getInitParameter(String name) {
        return parameters.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        Set<String> names = new HashSet<>(parameters.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return parameters.putIfAbsent(name, value) == null;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> names = new HashSet<>(attributes.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public String getServletContextName() {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return addServlet(servletName, className, null, null, null);
    }

    public ServletRegistration.Dynamic addServlet(String servletName, String servletClass, String url, Servlet servlet, Map<String, String> initParams) throws IllegalStateException {

        StandardWrapper wrapper = (StandardWrapper) context.findChild(servletName);

        // Assume a 'complete' ServletRegistration is one that has a class and
        // a name
        if (wrapper == null) {
            wrapper = new StandardWrapper();
            wrapper.setServletName(servletName);
            wrapper.setUrl(url);
            context.addChild(wrapper);
        } else {
            if (wrapper.getServletName() != null &&
                    wrapper.getServletClass() != null) {
                if (wrapper.isOverridable()) {
                    wrapper.setOverridable(false);
                } else {
                    return null;
                }
            }
        }

        ServletSecurity annotation = null;
        if (servlet == null) {
            wrapper.setServletClass(servletClass);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<?> clazz = null;
            try {
                clazz = classLoader.loadClass(servletClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz != null) {
                annotation = clazz.getAnnotation(ServletSecurity.class);
                try {
                    wrapper.setServlet((Servlet) clazz.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            wrapper.setServletClass(servlet.getClass().getName());
            wrapper.setServlet(servlet);
//            if (context.wasCreatedDynamicServlet(servlet)) {
            annotation = servlet.getClass().getAnnotation(ServletSecurity.class);
//            }
        }

        if (initParams != null) {
            for (Map.Entry<String, String> initParam : initParams.entrySet()) {
                wrapper.addInitParameter(initParam.getKey(), initParam.getValue());
            }
        }

        ServletRegistration.Dynamic registration = new ApplicationServletRegistration(wrapper);
        if (annotation != null) {
            registration.setServletSecurity(new ServletSecurityElement(annotation));
        }
        return registration;
    }

    public FilterRegistration.Dynamic addFilter(String filterName, String filterClass, Filter filter) throws IllegalStateException {

        FilterDef filterDef = context.findFilterDef(filterName);

        // Assume a 'complete' FilterRegistration is one that has a class and
        // a name
        if (filterDef == null) {
            filterDef = new FilterDef();
            filterDef.setFilterName(filterName);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<?> clazz = null;
            try {
                clazz = classLoader.loadClass(filterClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                filterDef.setFilter((Filter) clazz.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            context.addFilterDef(filterDef);
        } else {
            if (filterDef.getFilterName() != null &&
                    filterDef.getFilterClass() != null) {
                return null;
            }
        }

        if (filter == null) {
            filterDef.setFilterClass(filterClass);
        } else {
            filterDef.setFilterClass(filter.getClass().getName());
            filterDef.setFilter(filter);
        }
        return new ApplicationFilterRegistration(filterDef);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return addServlet(servletName, null, null, servlet, null);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return addServlet(servletName, servletClass.getName(), null, null, null);
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            @SuppressWarnings("unchecked")
            T servlet = (T) classLoader.loadClass(clazz.toString()).newInstance();
            StandardWrapper standardWrapper = new StandardWrapper();
            standardWrapper.setServletName(clazz.getName());
            standardWrapper.setServlet(servlet);
            standardWrapper.setServletClass(clazz.toString());
            context.addChild(standardWrapper);
            return servlet;
        } catch (ReflectiveOperationException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return null;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        return null;
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {

    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }

    @Override
    public void addListener(String className) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> clazz = null;
        try {
            clazz = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            context.setListenerContext(className, (ServletContextListener) clazz.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends EventListener> void addListener(T t) {

    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {

    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void declareRoles(String... roleNames) {

    }
}
