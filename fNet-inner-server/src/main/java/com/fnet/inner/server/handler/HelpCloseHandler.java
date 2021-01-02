package com.fnet.inner.server.handler;

import com.fnet.inner.server.service.InnerSender;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelpCloseHandler extends ChannelInboundHandlerAdapter {

    EventLoopGroup eventExecutors;

    public HelpCloseHandler(EventLoopGroup eventExecutors) {
        this.eventExecutors = eventExecutors;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (InnerSender.getInstance().getTransfer().getNumsOfTransferChannel() <= 0) {
            log.info("There are no TransferChannel available, so close event loop excutors!");
            if (!eventExecutors.isShutdown() && !eventExecutors.isShuttingDown()) {
                eventExecutors.shutdownGracefully();
            }
        }
        super.channelInactive(ctx);
    }
}
