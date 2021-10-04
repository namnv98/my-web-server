package com.nnv.core.filter;

import javax.servlet.*;

public interface AbstractFilter {
    default void init(FilterConfig filterConfig) throws ServletException {
    }

    void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3);

    default void destroy() {
    }
}
