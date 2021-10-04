package com.nnv.core.servlet.facade;

import com.nnv.cookie.MyCookie;
import com.nnv.core.context.output.MyPrintWriter;
import com.nnv.core.servlet.FileStream;
import com.nnv.core.servlet.Response;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class ResponseFacade implements HttpServletResponse {
    private final Response response;

    public void viewFile(String fileName) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        String path = url.getPath().substring(1);
        String mimeType = Files.probeContentType(Path.of(path));
        setContentType(mimeType);
        PrintStream out = new PrintStream(response.outputStream);
        out.write(response.header().getBytes());
        sendFile(out, path);
        out.flush();

        response.outputStream.close();
    }

    public void sendFile(PrintStream out, String path) {
        FileStream.stream(path, 30000).forEach(b -> {
            try {
                out.write(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Response getResponse() {
        return response;
    }

    public ResponseFacade(Response response) {
        this.response = response;

        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
        final int expiryTime = 60 * 60 * 24;
        MyCookie defCookie = new MyCookie("def", "hihi", tomorrow, expiryTime, "", "", false, false, "Strict");
        addCookie(defCookie);
    }

    @Override
    public void addCookie(Cookie cookie) {
        String cookieString = cookie.toString();
        response.cookies.put(cookie.getName(), cookie);
        addHeader("Set-Cookie", cookieString);
    }

    @Override
    public boolean containsHeader(String s) {
        return false;
    }

    @Override
    public String encodeURL(String s) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String s) {
        return null;
    }

    @Override
    public String encodeUrl(String s) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String s) {
        return null;
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {

    }

    @Override
    public void sendRedirect(String s) throws IOException {

    }

    @Override
    public void setDateHeader(String s, long l) {

    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String s, String s1) {
        response.headers.put(s, s1);
    }

    @Override
    public void addHeader(String s, String s1) {
        response.headers.put(s, s1);
    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int i) {
        response.status = i;
    }

    @Override
    public void setStatus(int i, String s) {

    }

    @Override
    public int getStatus() {
        return response.status;
    }

    @Override
    public String getHeader(String s) {
        return response.headers.get(s);
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return response.headers.keySet();
    }

    @Override
    public String getCharacterEncoding() {
        return response.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return response.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    @Override
    public MyPrintWriter getWriter() throws IOException {
        return response.getWriter();
    }

    @Override
    public void setCharacterEncoding(String s) {
        response.setCharacterEncoding(s);
    }

    @Override
    public void setContentLength(int i) {
        response.setContentLength(i);
    }

    @Override
    public void setContentType(String s) {
        response.setContentType(s);
    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

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

    @Override
    public void setLocale(Locale locale) {
        response.setLocale(locale);
    }

    @Override
    public Locale getLocale() {
        return response.getLocale();
    }
}
