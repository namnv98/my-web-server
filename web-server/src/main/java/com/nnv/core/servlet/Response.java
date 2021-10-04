package com.nnv.core.servlet;

import com.nnv.core.context.output.MyPrintWriter;
import com.nnv.core.context.output.MyServletOutputStream;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 把一个socket的OutputStream封装成一个Response对应。
 *
 * @author Liuweian
 */
public class Response implements ServletResponse {

    public OutputStream outputStream;

    private Request request;

    private MyPrintWriter myPrintWriter;

    private MyServletOutputStream myServletOutputStream;

    public Map<String, String> headers = new HashMap<>();

    public Map<String, Cookie> cookies = new HashMap<String, Cookie>();

    public int status = 200;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
        myServletOutputStream = new MyServletOutputStream(this);
        myPrintWriter = new MyPrintWriter(this);
    }

    public String header() {
        String CRLF = "\r\n";

        Map<String, Object> headerMap = new HashMap<>();
        headerMap.putAll(headers);

        headerMap.put("Connection", "Keep-Alive");
        headerMap.put("Date", new Date());
//      headerMap.put("Cache-control", "public,max-age=14400,public");

        String headerString = headerMap.entrySet().stream().map(m -> String.format("%s: %s" + CRLF, m.getKey(), m.getValue())).collect(Collectors.joining());

        String contents = String.format("%s %d" + CRLF, "HTTP/1.1", status);
        contents += headerString;
        contents += CRLF;
        return contents;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public String getCharacterEncoding() {
        return headers.get("Character-Encoding").toString();
    }

    @Override
    public String getContentType() {
        return headers.get("Content-Type");
    }

    @Override
    public MyServletOutputStream getOutputStream() throws IOException {
        return myServletOutputStream;
    }

    public MyPrintWriter getWriter() {
        return myPrintWriter;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        headers.put("Character-Encoding", charset);
    }

    @Override
    public void setContentLength(int len) {
        headers.put("Content-Length", String.valueOf(len));
    }

    @Override
    public void setContentType(String type) {
        headers.put("Content-Type", type);
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    Locale locale = Locale.getDefault();

    @Override
    public void setLocale(Locale loc) {
        locale = loc;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }


}
