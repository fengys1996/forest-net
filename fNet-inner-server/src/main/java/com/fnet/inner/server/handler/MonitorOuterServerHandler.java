package com.fnet.inner.server.handler;

import com.fnet.common.transfer.Transfer;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.inner.server.service.InnerSender;
import com.fnet.inner.server.messageResolver.ResolverContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.fnet.common.net.TcpServer.*;

@Slf4j
public class MonitorOuterServerHandler extends ChannelInboundHandlerAdapter {

    private static int numsOfActiveChannel = 0;
    private static final Object LOCK = new Object();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InnerSender innerSender = InnerSender.getInstance();
        Transfer transfer = innerSender.getTransfer();
        transfer.addTransferChannel(ctx.channel());

        synchronized (LOCK) {
            ++numsOfActiveChannel;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResolverContext.resolverMessage((Message) msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel inactive! Transfer channel is removed!");
        InnerSender.getInstance().getTransfer().removeTransferChannel(ctx.channel());
        ctx.channel().close();

        synchronized (LOCK) {
            if (--numsOfActiveChannel == 0) {
                if (!CONNECT_REAL_SERVER_EVENTLOOP_GROUP.isShutdown() && !CONNECT_REAL_SERVER_EVENTLOOP_GROUP.isShuttingDown()) {
                    log.info("There are no TransferChannel available, so close connect real server eventloop group!");
                    CONNECT_REAL_SERVER_EVENTLOOP_GROUP.shutdownGracefully();
                }
                if (!CONNECT_OUTER_SERVER_EVENTLOOP_GROUP.isShutdown() && !CONNECT_OUTER_SERVER_EVENTLOOP_GROUP.isShuttingDown()) {
                    log.info("There are no TransferChannel available, so close connect outer server eventloop group!");
                    CONNECT_OUTER_SERVER_EVENTLOOP_GROUP.shutdownGracefully();
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (cause instanceof IOException) {
            if ("远程主机强迫关闭了一个现有的连接。".equals(cause.getMessage())) {
                log.info("The remote host forced an existing connection to close.");
                return;
            }
        }
        ctx.fireExceptionCaught(cause);
    }
}
