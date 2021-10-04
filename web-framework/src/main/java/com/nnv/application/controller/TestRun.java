package com.nnv.application.controller;

import javax.servlet.AsyncContext;

public class TestRun implements Runnable {
    AsyncContext ctx;

    public TestRun(AsyncContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10000L);
            ctx.complete();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
