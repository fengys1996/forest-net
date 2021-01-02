package com.fnet.common.service;

import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadPoolUtil {

    private static class NewThreadPolicy implements RejectedExecutionHandler {

        public NewThreadPolicy() { }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            log.info("common thread pool resource insufficient, so create a new thread to excute!");
            new Thread(r).start();
        }
    }

    private static int availableProcessors = NettyRuntime.availableProcessors();
    private static final ExecutorService commonExecutor = new ThreadPoolExecutor(availableProcessors, availableProcessors * 2,
                                                                                  5L, TimeUnit.SECONDS,
                                                                                 new LinkedBlockingQueue<Runnable>(1000),
                                                                                 new DefaultThreadFactory("commonThreadPool"),
                                                                                 new NewThreadPolicy());

    public static ExecutorService getCommonExecutor() {
        return commonExecutor;
    }
}
