package com.nnv.core.servlet.facade;

import com.nnv.core.context.StandardContext;
import com.nnv.core.servlet.Request;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.naming.Context;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


public class AsyncContextImpl implements AsyncContext {
    private volatile Request request;


    public AsyncContextImpl(Request request) {
        this.request = request;
    }

    @Override
    public ServletRequest getRequest() {
        return servletRequest;
    }

    @Override
    public ServletResponse getResponse() {
        return servletResponse;
    }

    @Override
    public boolean hasOriginalRequestAndResponse() {
        return false;
    }

    @Override
    public void dispatch() {

    }

    @Override
    public void dispatch(String path) {

    }

    @Override
    public void dispatch(ServletContext context, String path) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void start(Runnable run) {

    }
    private static class RunnableWrapper implements Runnable {

        private final Runnable wrapped;
        private final Context context;
        private final Request coyoteRequest;

        public RunnableWrapper(Runnable wrapped, Context ctxt,
                               Request coyoteRequest) {
            this.wrapped = wrapped;
            this.context = ctxt;
            this.coyoteRequest = coyoteRequest;
        }

        @Override
        public void run() {

        }
    }

    @Override
    public void addListener(AsyncListener listener) {

    }

    @Override
    public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {

    }

    @Override
    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public void setTimeout(long timeout) {
        timeout = timeout;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }


    private final Object asyncContextLock = new Object();

    private volatile ServletRequest servletRequest = null;
    private volatile ServletResponse servletResponse = null;
    private boolean hasOriginalRequestAndResponse = true;
    private volatile Runnable dispatch = null;
    private StandardContext context = null;
    // Default of 30000 (30s) is set by the connector
    private long timeout = -1;
    private AsyncEvent event = null;
    private final List<AsyncListenerWrapper> listeners = new ArrayList<>();

    public void setStarted(StandardContext context, ServletRequest request, ServletResponse response, boolean originalRequestResponse) {

        synchronized (asyncContextLock) {
//            this.request.getCoyoteRequest().action(ActionCode.ASYNC_START, this);

            this.context = context;
            this.servletRequest = request;
            this.servletResponse = response;
            this.hasOriginalRequestAndResponse = originalRequestResponse;
            this.event = new AsyncEvent(this, request, response);

            List<AsyncListenerWrapper> listenersCopy = new ArrayList<>(listeners);
            listeners.clear();

            for (AsyncListenerWrapper listener : listenersCopy) {
                try {
                    listener.fireOnStartAsync(event);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

}