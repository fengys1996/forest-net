package com.fnet.common.tool;

import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadPoolTool {

    private static class NewThreadPolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            log.info("common thread pool resource insufficient, so create a new thread to excute!");
            new Thread(r).start();
        }
    }

    private static final int AVAILABLE_PROCESSORS = NettyRuntime.availableProcessors();
    private static final ExecutorService COMMON_EXECUTOR = new ThreadPoolExecutor(AVAILABLE_PROCESSORS, AVAILABLE_PROCESSORS * 2,
                                                     5L, TimeUnit.SECONDS,
                                                     new LinkedBlockingQueue<Runnable>(1000),
                                                     new DefaultThreadFactory("commonThreadPool"),
                                                     new NewThreadPolicy());

    public static ExecutorService getCommonExecutor() {
        return COMMON_EXECUTOR;
    }
}
