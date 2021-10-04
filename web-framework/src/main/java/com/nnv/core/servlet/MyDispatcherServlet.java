package com.nnv.core.servlet;


import com.nnv.core.anotation.MyController;
import com.nnv.core.anotation.MyRequestMapping;
import com.nnv.core.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class MyDispatcherServlet extends FrameworkServlet {
    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = MyDispatcherServlet.class.getName() + ".CONTEXT";

    public static final String DISPLAY_APPLICATION_CONTEXT = "My Dispatcher Servlet";

    private Map<String, Method> handlerMapping = new HashMap<>();

    private Map<String, Object> controllerMap = new HashMap<>();

    public MyDispatcherServlet() {
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void onRefresh(ApplicationContext context) {
        context.setDisplayName(DISPLAY_APPLICATION_CONTEXT);
        getServletContext().setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
        initHandlerMapping(context);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doDispatch(request, response);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if (handlerMapping.isEmpty()) {
            return;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();

        url = url.replace(contextPath, "").replaceAll("/+", "/");
        url = url.replace(servletPath, "").replaceAll("/+", "/");

        if (!this.handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 NOT FOUND!");
            return;
        }

        Method method = this.handlerMapping.get(url);

        Class<?>[] parameterTypes = method.getParameterTypes();

        Map<String, String[]> parameterMap = req.getParameterMap();

        Object[] paramValues = new Object[parameterTypes.length];

        //方法的参数列表
        for (int i = 0; i < parameterTypes.length; i++) {
            //根据参数名称，做某些处理
            String requestParam = parameterTypes[i].getSimpleName();


            if (requestParam.equals("HttpServletRequest")) {
                //参数类型已明确，这边强转类型
                paramValues[i] = req;
                continue;
            }
            if (requestParam.equals("HttpServletResponse")) {
                paramValues[i] = resp;
                continue;
            }
            if (requestParam.equals("String")) {
                for (Entry<String, String[]> param : parameterMap.entrySet()) {
                    String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                    paramValues[i] = value;
                }
            }
            if (requestParam.equals("String[]")) {
                for (Entry<String, String[]> param : parameterMap.entrySet()) {
                    paramValues[i] = param.getValue();
                }
            }
        }
        try {
            method.invoke(this.controllerMap.get(url), paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHandlerMapping(ApplicationContext context) {
        if (context.getBeanFactory().beans.isEmpty()) {
            return;
        }
        try {
            for (Object entry : context.findBeanByAnnotation(MyController.class)) {
                Class<? extends Object> clazz = entry.getClass();
                String baseUrl = "";
                if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                    MyRequestMapping annotation = clazz.getAnnotation(MyRequestMapping.class);
                    baseUrl = annotation.value();
                }
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                        continue;
                    }
                    MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                    String url = annotation.value();

                    url = (baseUrl + "/" + url).replaceAll("/+", "/");
                    handlerMapping.put(url, method);
                    controllerMap.put(url, clazz.newInstance());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}