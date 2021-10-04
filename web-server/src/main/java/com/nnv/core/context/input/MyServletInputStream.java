package com.nnv.core.context.input;

import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyServletInputStream extends ServletInputStream {

    private InputStream inputStream;

    public MyServletInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }
}
