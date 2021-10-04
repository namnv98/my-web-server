package com.nnv.server;

import com.nnv.RunnableWorker;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author harisk
 */
public class ThreadPooledServer implements Runnable {

    private int serverPort;
    private ServerSocket serverSocket;
    private boolean isStopped = false;
    private ExecutorService threadPool;

    private boolean SSLEnabled = false;
    private String keystoreFile = StringUtils.EMPTY;
    private String keyStorePassword = StringUtils.EMPTY;
    private String keyStoreType = StringUtils.EMPTY;


    private void initProperties() {
        String filePath = Objects.requireNonNull(getClass().getClassLoader().getResource("config.properties")).getPath();
        try (InputStream input = new FileInputStream(filePath)) {
            Properties prop = new Properties();
            prop.load(input);
            SSLEnabled = Boolean.parseBoolean(prop.getProperty("SSLEnabled"));
            keystoreFile = Objects.requireNonNull(getClass().getClassLoader().getResource(prop.getProperty("keystoreFile"))).getPath().substring(1);
            keyStorePassword = prop.getProperty("keystorePass");
            keyStoreType = prop.getProperty("keyStoreType");
            serverPort = Integer.parseInt(prop.getProperty("port"));
            threadPool = Executors.newFixedThreadPool(Integer.parseInt(prop.getProperty("poolSize")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public ThreadPooledServer() {
        initProperties();
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error on stop server", e);
        }
    }

    private void openServerSocket() {
        try {
            if (SSLEnabled) {
                System.setProperty("javax.net.ssl.keyStore", keystoreFile);
                System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
                System.setProperty("javax.net.ssl.keyStoreType", keyStoreType);

                SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                serverSocket = factory.createServerSocket(this.serverPort);
            } else {
                this.serverSocket = new ServerSocket(this.serverPort);
            }
            System.out.println("Server is running on port " + this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + this.serverPort, e);
        }
    }

    @Override
    public void run() {
        try {
            openServerSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server stopped");
                    return;
                }
                throw new RuntimeException("Error on accept client connection", e);
            }

            /**
             * Push job to threadpool, threadpool will select a free thread
             * to handle this job, if all threads're busy, job will be in queue
             * and wait until exist a free thread
             */
            this.threadPool.execute(new RunnableWorker(clientSocket));
        }

        /**
         * Shutdown threadpool
         */
        this.threadPool.shutdown();

        System.out.println("Server stopped");
    }
}