
package com.nnv;

import com.nnv.core.context.ContextUtils;
import com.nnv.core.context.MyServletContext;
import com.nnv.core.context.filter.ApplicationFilterChain;
import com.nnv.core.context.filter.ApplicationFilterConfig;
import com.nnv.core.context.filter.FilterDef;
import com.nnv.core.context.servlet.StandardWrapper;
import com.nnv.core.servlet.Request;
import com.nnv.core.servlet.Response;
import com.nnv.core.servlet.facade.RequestFacade;
import com.nnv.core.servlet.facade.ResponseFacade;
import com.nnv.loader.MyWebAppLoader;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class RunnableWorker implements Runnable {

    private Socket client;
    private RequestFacade requestFacade = null;
    private ResponseFacade responseFacade = null;

    public RunnableWorker(Socket client) {
        this.client = client;
        try {
            Request request = new Request(client.getInputStream(), ContextUtils.getInstance());
            Response response = new Response(client.getOutputStream());

            requestFacade = new RequestFacade(request);
            responseFacade = new ResponseFacade(response);

            String clientIP = client.getInetAddress().toString();
            int clientPort = client.getPort();

            request.header.put("clientIP", clientIP);
            request.header.put("clientPort", String.valueOf(clientPort));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            if (requestFacade.getRequestURI() != null) {
                invoke();
                if (requestFacade.getMethod().equals("POST")) {
                    responseFacade.getResponse().outputStream.write(responseFacade.getResponse().header().getBytes());
                }
            }
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void invoke() throws IOException, ServletException {
        String url = requestFacade.getRequestURI();
        if (url == null) {
            return;
        }

        String appName = getAppName(url);

        MyServletContext applicationContext = ContextUtils.getInstance().getApplicationContext(appName);

        if (applicationContext == null) {
            responseFacade.setStatus(404);
            responseFacade.getResponse().outputStream.write(responseFacade.getResponse().header().getBytes());
            return;
        }

        Thread.currentThread().setContextClassLoader(new MyWebAppLoader(applicationContext.getContext().getUrlPattern()));

        //filter
        HttpServlet httpServlet = (HttpServlet) createService(url, appName, applicationContext);
        if (httpServlet == null) {
            //return 404
            return;
        }

        ApplicationFilterChain applicationFilterChain = createFilterChain(applicationContext, httpServlet);
        applicationFilterChain.internalDoFilter(requestFacade, responseFacade);
    }

    private Servlet createService(String url, String appName, MyServletContext servletContext) throws ServletException {
        ArrayList<StandardWrapper> servletDefsList = servletContext.getContext().getServletDefsList();
        Optional<StandardWrapper> wrapperOptional = servletDefsList.stream().filter(s -> s.checkMatchUrl(url, appName)).findAny();
        if (wrapperOptional.isEmpty()) {
            return null;
        }
        StandardWrapper servletDef = wrapperOptional.get();
        requestFacade.setServletPath(servletDef.getUrl());
        HttpServlet httpServlet = (HttpServlet) servletDef.getInstance();
        if (servletDef.isInit()) {
            httpServlet.init(createServletConfig(servletDef, servletContext));
            servletDef.setInit(false);
        }
        return httpServlet;
    }

    private ApplicationFilterChain createFilterChain(MyServletContext servletContext, Servlet servlet) throws ServletException, IOException {
        ApplicationFilterChain applicationFilterChain = new ApplicationFilterChain();
        applicationFilterChain.setServlet(servlet);
        List<FilterDef> filterDefs = servletContext.getContext().getFilterDefsList();
        for (FilterDef filterDef : filterDefs) {
            Filter filter = filterDef.getFilter();
            if (filterDef.isInit()) {
                filter.init(createFilterConfig(filterDef, servletContext));
                filterDef.setInit(false);
            }
            applicationFilterChain.addFilter(new ApplicationFilterConfig(filter));
        }
        return applicationFilterChain;
    }

    private FilterConfig createFilterConfig(FilterDef filterDef, ServletContext servletContext) {
        FilterConfig filterConfig = new FilterConfig() {
            @Override
            public String getFilterName() {
                return filterDef.getFilterName();
            }

            @Override
            public javax.servlet.ServletContext getServletContext() {
                return servletContext;
            }

            @Override
            public String getInitParameter(String name) {
                return filterDef.getParameters().get(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                Set<String> names = new HashSet<>(filterDef.getParameters().keySet());
                return Collections.enumeration(names);
            }
        };
        return filterConfig;
    }

    private ServletConfig createServletConfig(StandardWrapper standardWrapper, ServletContext applicationContext) {
        ServletConfig servletConfig = new ServletConfig() {
            @Override
            public String getServletName() {
                return standardWrapper.getServletName();
            }

            @Override
            public javax.servlet.ServletContext getServletContext() {
                return applicationContext;
            }

            @Override
            public String getInitParameter(String s) {
                return standardWrapper.getParameters().get(s);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                Set<String> names = new HashSet<>(standardWrapper.getParameters().keySet());
                return Collections.enumeration(names);
            }
        };
        return servletConfig;
    }

    private String getAppName(String url) {
        String[] p = url.split("/");
        return p[1];
    }

}