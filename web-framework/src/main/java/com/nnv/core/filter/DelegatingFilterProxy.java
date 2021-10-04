package com.nnv.core.filter;

import com.nnv.core.context.WebApplicationContext;
import com.nnv.core.context.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DelegatingFilterProxy implements Filter {
    List<AbstractFilter> filterList = new ArrayList<>();
    WebApplicationContext webApplicationContext;

    private void findFilterList() {
        filterList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : webApplicationContext.getBeanFactory().beans.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof AbstractFilter) {
                filterList.add((AbstractFilter) value);
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        webApplicationContext = WebApplicationContextUtils.findWebApplicationContext(servletRequest.getServletContext());

        findFilterList();
        for (AbstractFilter filter : filterList) {
            filter.doFilter(servletRequest, servletResponse, filterChain);
        }

        //before filter
        filterChain.doFilter(servletRequest, servletResponse);
        //after filter
    }

    @Override
    public void destroy() {

    }
}
