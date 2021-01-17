package com.fnet.inner.server.handler;

import com.fnet.common.service.Sender;
import com.fnet.common.tool.ThreadPoolTool;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import com.fnet.inner.server.sender.TransferCache;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

import static com.fnet.common.net.TcpServer.*;

@Slf4j
@Sharable
public class MonitorOuterServerHandler extends ChannelInboundHandlerAdapter {

    public Sender sender;
    MessageResolver resolver;

    public MonitorOuterServerHandler(Sender sender, MessageResolver resolver) {
        this.sender = sender;
        this.resolver = resolver;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Transfer channel connect!");
        TransferCache.addTransferChannel(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        resolver.resolve((Message) msg, sender);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Transfer channel disconnect!");
        TransferCache.removeTransferChannel();
        Channel channel = ctx.channel();
        channel.close();
        doSomethingAfterAllTransferChannelInactive();
    }

    private void doSomethingAfterAllTransferChannelInactive() {
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
        commonExecutor = ThreadPoolTool.getCommonExecutor();
        if (commonExecutor != null && !commonExecutor.isShutdown()) {
            ThreadPoolTool.getCommonExecutor().shutdownNow();
        }
    }
}
