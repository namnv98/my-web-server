package com.nnv.core.context.input;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyBufferedInputStream extends BufferedInputStream {
    private volatile boolean decoratedOpen = true;
    public MyBufferedInputStream(InputStream source) {
        super(source);
        super.mark(Integer.MAX_VALUE);
    }
    @Override
    public synchronized void close() throws IOException {
        if (decoratedOpen) {
            decoratedOpen = false;
            in.close();
        }
        super.reset();
    }
}