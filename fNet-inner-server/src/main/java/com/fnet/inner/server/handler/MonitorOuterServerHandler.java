package com.fnet.inner.server.handler;

import com.fnet.common.handler.AbstractMonitorHandler;
import com.fnet.common.service.Sender;
import com.fnet.common.service.ThreadPoolUtil;
import com.fnet.common.transfer.Resolver;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

import static com.fnet.common.net.TcpServer.*;

@Slf4j
public class MonitorOuterServerHandler extends AbstractMonitorHandler {

    public MonitorOuterServerHandler(Sender sender, Resolver resolver) {
        super(sender, resolver);
    }

    @Override
    public void doSomethingAfterAllTransferChannelInactive() {
        // close inner server
        if (!CONNECT_REAL_SERVER_EVENTLOOP_GROUP.isShutdown() && !CONNECT_REAL_SERVER_EVENTLOOP_GROUP.isShuttingDown()) {
            log.info("There are no TransferChannel available, so close connect real server eventloop group!");
            CONNECT_REAL_SERVER_EVENTLOOP_GROUP.shutdownGracefully();
        }
        if (!CONNECT_OUTER_SERVER_EVENTLOOP_GROUP.isShutdown() && !CONNECT_OUTER_SERVER_EVENTLOOP_GROUP.isShuttingDown()) {
            log.info("There are no TransferChannel available, so close connect outer server eventloop group!");
            CONNECT_OUTER_SERVER_EVENTLOOP_GROUP.shutdownGracefully();
        }
        ExecutorService commonExecutor;
        commonExecutor = ThreadPoolUtil.getCommonExecutor();
        if (commonExecutor != null && !commonExecutor.isShutdown()) {
            ThreadPoolUtil.getCommonExecutor().shutdownNow();
        }
    }
}
