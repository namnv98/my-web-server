package com.nnv.core.context.output;

import com.nnv.core.servlet.Response;

import java.io.IOException;
import java.io.PrintWriter;

public class MyPrintWriter extends PrintWriter {
    OutputBuffer outputBuffer;

    @Override
    public void close() {
        try {
            outputBuffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MyPrintWriter(Response response) {
        super(response.outputStream);
        outputBuffer = new OutputBuffer(response);
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        outputBuffer.write(cbuf, off, len);
    }

    @Override
    public void write(String s, int off, int len) {
        outputBuffer.write(s, off, len);
    }

    @Override
    public void write(int c) {
        outputBuffer.write(c);
    }

    @Override
    public void write(String c) {
        outputBuffer.write(c);
    }
}