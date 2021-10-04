package com.nnv.application.filer;

import com.nnv.core.anotation.Bean;
import com.nnv.core.filter.AbstractFilter;

import javax.servlet.*;

@Bean
public class CustomFiler1 implements AbstractFilter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        AbstractFilter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3) {
        System.out.println("filer 1 ok");
    }

    @Override
    public void destroy() {
        AbstractFilter.super.destroy();
    }
}
