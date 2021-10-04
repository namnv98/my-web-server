package com.nnv.core.servlet;

import com.nnv.core.context.ContextUtils;
import com.nnv.core.context.input.HttpInputStream;
import com.nnv.core.context.input.MyBufferedInputStream;
import com.nnv.core.context.input.MyServletInputStream;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 把一个socket封装成一个request。
 * 从socket的inputStream中获取对应的信息。
 *
 * @author Liuweian
 */
public class Request implements ServletRequest {
    private int maxHttpHeaderSize = 8 * 1024;
    long position = 0;

    private ContextUtils context;
    private Map<String, Object> attributes = new ConcurrentHashMap<>();
    public Map<String, String[]> paramMap = new HashMap<>();
    public Map<String, String> header = new HashMap<>();

    private String uri;
    private String method;
    private String httpVersion;

    private MyBufferedInputStream bufferedInputStream;

    public Request(InputStream inputStream, ContextUtils context) {
        this.context = context;
        this.bufferedInputStream = new MyBufferedInputStream(inputStream);
        try {
            Map<String, ArrayList> map = parseParam(bufferedInputStream);
            for (Map.Entry<String, ArrayList> e : map.entrySet()) {
                paramMap.put(e.getKey(), (String[]) e.getValue().toArray(String[]::new));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (position > maxHttpHeaderSize) {
            throw new RuntimeException("Request header is too large");
        }
    }

    private String getAppName(String url) {
        String[] p = url.split("/");
        return p[1];
    }

    public Map<String, ArrayList> parseParam(MyBufferedInputStream is) throws IOException {
        Map<String, ArrayList> paramMap = new HashMap<String, ArrayList>();
        BufferedReader lr = new BufferedReader(new InputStreamReader(is));

        String inputLine = null;

        // read request line
        try {
            inputLine = lr.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputLine == null) {
            return new HashMap<>();
        }
        System.out.println(inputLine);
        String[] requestCols = inputLine.split("\\s");
        method = requestCols[0];
        uri = requestCols[1];
        httpVersion = requestCols[2];

        // parse GET param
        if (uri.contains("?")) {
            paramMap.putAll(splitParam(uri.split("\\?", 2)[1], false));
        }

        // read header
        while (StringUtils.isNotBlank(inputLine = lr.readLine())) {
            position += inputLine.getBytes().length;
            int index = inputLine.indexOf(":");
            String key = inputLine.substring(0, index);
            String value = inputLine.substring(key.length() + 2);
            header.put(key, value);
            System.out.println("header line:\t" + inputLine);
        }

        // read body - POST method
        if ("POST".equalsIgnoreCase(method)) {

            StringBuffer bodySb = new StringBuffer();

            InputStream inputStream = new HttpInputStream(lr, header);

            int c;
            while ((c = inputStream.read()) != -1) {
                bodySb.append((char) c);
            }
            paramMap.putAll(splitParam(bodySb.toString(), true));
            System.out.println("post body:\t" + bodySb.toString());
        }

        return paramMap;
    }


    public Map<String, ArrayList> splitParam(String paramInput, boolean isBody) {
        Map<String, ArrayList> map = new HashMap<>();
        String[] paramPairs = paramInput.trim().split("&");
        for (int i = 0; i < paramPairs.length; i++) {
            String params = paramPairs[i];
            String[] kv = params.split("=");
            if (!isBody) {
                if (params.contains(",")) {
                    String[] vs = kv[1].split(",");
                    map.put(kv[0], new ArrayList<>(Arrays.asList(vs)));
                    continue;
                }
            }
            if (map.get(kv[0]) != null) {
                map.put(kv[0], map.get(kv[0])).add(kv[1]);
            } else {
                map.put(kv[0], new ArrayList<>(Arrays.asList(kv[1])));
            }
        }
        return map;
    }

    public Map<String, String> parseParam(String paramStr, boolean isBody) {
        String[] paramPairs = paramStr.trim().split("&");
        Map<String, String> paramMap = new HashMap<String, String>();

        String[] paramKv;
        for (String paramPair : paramPairs) {
            if (paramPair.contains("=")) {
                paramKv = paramPair.split("=");
                if (isBody) {
                    // replace '+' to ' ', because in body ' ' is replaced by '+' automatically when post,
                    paramKv[1] = paramKv[1].replace("+", " ");
                }
                paramMap.put(paramKv[0], paramKv[1]);
            }
        }
        return paramMap;
    }

    public String getUri() {
        if (uri == null) {
            return null;
        }
        if (uri.contains("?")) {
            uri = uri.substring(0, uri.indexOf("?"));
        }
        return uri;
    }

    public String getMethod() {
        return method;
    }


    @Override
    public Object getAttribute(String name) {
        return context.getApplicationContext(getAppName(getUri())).getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> names = new HashSet<>(attributes.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        bufferedInputStream.reset();
        return new MyServletInputStream(bufferedInputStream);
    }

    @Override
    public String getParameter(String name) {
        ArrayList<String> values = new ArrayList<>(Arrays.asList(paramMap.get(name)));
        if (values != null) {
            if (values.size() == 0) {
                return "";
            }
            return values.get(0);
        }
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(paramMap.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return paramMap.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return paramMap;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return Integer.parseInt(header.get("Host").split(":")[1]);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        bufferedInputStream.reset();
        return new BufferedReader(new InputStreamReader(bufferedInputStream));
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return header.get("Host");
    }

    @Override
    public void setAttribute(String name, Object o) {
        if (o == null) {
            return;
        }
        attributes.put(name, o);
        context.getApplicationContext(getAppName(getUri())).setAttribute(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }


    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return header.get("clientIP");
    }

    @Override
    public int getLocalPort() {
        return Integer.parseInt(header.get("clientPort"));
    }

    @Override
    public ServletContext getServletContext() {
        return context.getApplicationContext(getAppName(getUri()));
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }
}
