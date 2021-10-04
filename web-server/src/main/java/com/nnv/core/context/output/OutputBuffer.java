package com.nnv.core.context.output;

import com.nnv.core.servlet.Response;

import java.io.*;

public class OutputBuffer extends Writer {

    public static final int DEFAULT_BUFFER_SIZE = 8192;
    private ByteArrayOutputStream baos = null;
    private int bufferSize = DEFAULT_BUFFER_SIZE;
    private static final String CR = System.getProperty("line.separator", "\n");

    private Response response;
    private PrintStream printStream;
    private int countCommit = 0;

    public OutputBuffer(int bufferSize) {
        baos = new ByteArrayOutputStream();
        this.bufferSize = bufferSize;
    }

    void checkCommit() {
        if (getBuffer().size() >= bufferSize) {
            try {
                if (countCommit == 0) {
                    response.headers.put("Transfer-Encoding", "chunked");
                    printStream.write(response.header().getBytes());
                }
                printStream.print(Integer.toHexString(getBuffer().size()));
                printStream.print("\r\n");
                printStream.write(baos.toByteArray());

                printStream.print("\r\n");
                printStream.flush();

                countCommit++;

                baos.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() throws IOException {
        if (countCommit == 0) {
            response.setContentLength(baos.size());
            printStream.write(response.header().getBytes());
        }
        if(baos.size()>0){
            if (countCommit == 0) {
                printStream.write(baos.toByteArray());
            }else{
                printStream.print(Integer.toHexString(getBuffer().size()));
                printStream.print("\r\n");
                printStream.write(baos.toByteArray());
                printStream.print("\r\n");
                printStream.flush();
            }
        }

        printStream.print("0\r\n");
        printStream.print("\r\n");
        printStream.flush();
        baos.reset();
    }

    public OutputBuffer(Response response) {
        this.response = response;
        this.printStream = new PrintStream(response.outputStream);
        baos = new ByteArrayOutputStream();
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        baos.write(new String(cbuf).getBytes(), off, len);
        checkCommit();
    }

    @Override
    public void write(String c, int off, int len) {
        baos.write(c.getBytes(), off, len);
        checkCommit();
    }

    @Override
    public void write(String c) {
        try {
            baos.write(c.getBytes());
            checkCommit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() throws IOException {
        baos.flush();
    }


    public void write(byte[] b, int off, int len) {
        baos.write(b, off, len);
        checkCommit();
    }

    public void write(byte[] b) throws IOException {
        baos.write(b);
        checkCommit();
    }

    @Override
    public void write(int b) {
        baos.write(b);
        checkCommit();
    }

    public void print(boolean arg0) throws IOException {
        baos.write(("" + arg0).getBytes());
        checkCommit();
    }

    public void print(char c) throws IOException {
        baos.write(("" + c).getBytes());
        checkCommit();
    }

    public void print(double d) throws IOException {
        baos.write(("" + d).getBytes());
        checkCommit();
    }

    public void print(float f) throws IOException {
        baos.write(("" + f).getBytes());
        checkCommit();
    }

    public void print(int i) throws IOException {
        baos.write(("" + i).getBytes());
        checkCommit();
    }

    public void print(long l) throws IOException {
        baos.write(("" + l).getBytes());
        checkCommit();
    }

    public void print(String arg0) throws IOException {
        baos.write((arg0).getBytes());
        checkCommit();
    }


    public void println() throws IOException {
        baos.write((CR).getBytes());
        checkCommit();
    }

    public void println(boolean b) throws IOException {
        baos.write(("" + b + CR).getBytes());
        checkCommit();
    }

    public void println(char c) throws IOException {
        baos.write(("" + c + CR).getBytes());
        checkCommit();
    }

    public void println(double d) throws IOException {
        baos.write(("" + d + CR).getBytes());
        checkCommit();
    }

    public void println(float f) throws IOException {
        baos.write(("" + f + CR).getBytes());
        checkCommit();
    }

    public void println(int i) throws IOException {
        baos.write(("" + i + CR).getBytes());
        checkCommit();
    }

    public void println(long l) throws IOException {
        baos.write(("" + l + CR).getBytes());
        checkCommit();
    }

    public void println(String s) throws IOException {
        baos.write((s + CR).getBytes());
        checkCommit();
    }

    /**
     * @return the bufferSize
     */
    public int getBufferSize() {
        return bufferSize;
    }

    public ByteArrayOutputStream getBuffer() {
        return baos;
    }

    public void setBuffer(ByteArrayOutputStream baos) {
        this.baos = baos;
    }
}
