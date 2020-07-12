package com.fnet.server.handler;

import com.fnet.common.handler.CommonHandler;
import com.fnet.common.transferProtocol.Message;
import com.fnet.server.messageResolver.ResolverContext;
import io.netty.channel.ChannelHandlerContext;

public class MonitorInnerServerHandler extends CommonHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResolverContext.resolverMessage((Message) msg);
    }
}
