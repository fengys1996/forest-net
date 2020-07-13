package com.fnet.inner.server.handler;

import com.fnet.common.handler.CommonHandler;
import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.inner.server.data.Sender;
import com.fnet.inner.server.messageResolver.ResolverContext;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MonitorOuterServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        Sender.sendRegisterMessage(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        final Message msg1 = (Message) msg;
//        System.out.println(new String(msg1.getData()));
        System.out.println("accept!");
//        Message message = new Message(MessageType.HEART_BEAT, 0, null);
//        ctx.writeAndFlush(message).addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                System.out.println("complete");
//            }
//        });
//        ResolverContext.resolverMessage((Message) msg);
    }
}
