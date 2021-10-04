package com.nnv.core.context.filter;

import javax.servlet.Filter;

public class ApplicationFilterConfig {
    private Filter filter;

    public ApplicationFilterConfig(Filter filter) {
        this.filter = filter;
    }

    public Filter getFilter() {
        return filter;
    }
}
