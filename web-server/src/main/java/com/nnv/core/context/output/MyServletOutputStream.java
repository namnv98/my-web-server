package com.nnv.core.context.output;

import com.nnv.core.servlet.Response;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class MyServletOutputStream extends ServletOutputStream {
    OutputBuffer outputBuffer;
    Response response;

    public MyServletOutputStream(Response response) {
        this.response = response;
        outputBuffer = new OutputBuffer(response);
    }

    @Override
    public void close() {
        try {
            outputBuffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(int b) {
        outputBuffer.write(b);
    }

    public void write(byte[] b) throws IOException {
        outputBuffer.write(b);
    }
}