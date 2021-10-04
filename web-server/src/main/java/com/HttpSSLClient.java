package com;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

public class HttpSSLClient {

    private static SSLSocketFactory getFactorySimple() throws Exception {

        File crtFile = new File("D:\\java\\my-web-server\\web-server\\src\\main\\resources\\ssl\\local-cert.crt");
        Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(new FileInputStream(crtFile));

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("server", certificate);

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }

    public static void main(String[] args) throws Exception {
        post();
    }

    public static void post() throws Exception {
        SSLSocketFactory factory = getFactorySimple();
        SSLSocket sslsocket = (SSLSocket) factory.createSocket("localhost", 8092);

        sslsocket.startHandshake();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userName", "me@example.com");
        params.put("password", "12345");

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> item : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(item.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(item.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        PrintWriter pw = new PrintWriter(sslsocket.getOutputStream());
        //-- request line
        pw.println("POST /testDemo1/app1/home/employee/create HTTP/1.1");
        //-- request header
        pw.println("Host: localhost:8092");
        pw.println("Accept-Encoding: gzip, deflate, br");
        pw.println("Accept-Language: en,vi;q=0.9");
        pw.println("Accept: text/html,application/xhtml+xml,application/xml");
        pw.println("Cache-Control: max-age=0");
        pw.println("User-Agent: Java Client 1.0");
        pw.println("Connection: keep-alive");
        pw.println("Content-Type: application/x-www-form-urlencoded");
        pw.println("Content-Length: " + postDataBytes.length);
        //-- a blank line separates header & body
        pw.println("");
        //-- request message body
        pw.println(postData);

        pw.flush();

        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
        String strCurrentLine;
        while ((strCurrentLine = br.readLine()) != null) {
            System.out.println(strCurrentLine);
        }
        sslsocket.close();
    }


    public static void get() throws Exception {
        SSLSocketFactory factory = getFactorySimple();
        SSLSocket sslsocket = (SSLSocket) factory.createSocket("localhost", 8092);
//        Socket socket=new Socket("localhost",8092);

        sslsocket.startHandshake();

        PrintWriter pw = new PrintWriter(sslsocket.getOutputStream());
        //--request line
        pw.println("GET /testDemo1/app1/home/index HTTP/1.1");
        //--request header
        pw.println("Host: localhost:8092");
        pw.println("Accept-Encoding: gzip, deflate, br");
        pw.println("Accept-Language: en,vi;q=0.9");
        pw.println("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        pw.println("Cache-Control: max-age=0");
        pw.println("User-Agent: Java Client 1.0");
        pw.println("Connection: keep-alive");
        //--a blank line separates header & body
        pw.println("");

        pw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            output.append(line).append(System.lineSeparator());
        }

        System.out.println(output);
        sslsocket.close();
    }
}