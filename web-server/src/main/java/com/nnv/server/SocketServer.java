package com.nnv.server;

import com.nnv.InitServlet;

public class SocketServer {
    public static void main(String[] args) {
        InitServlet servlet = new InitServlet();
        servlet.init();

        ThreadPooledServer server = new ThreadPooledServer();
        new Thread(server).start();
    }

    private void stopServer(ThreadPooledServer server) {
        /**
         * Waiting 20s before stopping server
         */
        try {
            Thread.sleep(20 * 1000);
            server.stop();
            System.exit(0);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
