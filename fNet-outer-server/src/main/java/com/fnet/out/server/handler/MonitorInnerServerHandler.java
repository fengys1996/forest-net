package com.fnet.out.server.handler;

import com.fnet.common.handler.CommonHandler;
import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.out.server.data.TransferChannelData;
import com.fnet.out.server.messageResolver.ResolverContext;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

public class MonitorInnerServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active! add transfer channel is added! The hashCode is " + ctx.channel().hashCode());
        TransferChannelData.getInstance().addTransferChannel(ctx.channel());

        Message message = new Message(MessageType.HEART_BEAT, 0, "111111111111111111111111111\r".getBytes());
        for (int i = 0 ; i<10 ; i++) {
            TransferChannelData.getInstance().getReadyTransferChannel().writeAndFlush(message).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("complete");
                }
            });
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ResolverContext.resolverMessage((Message) msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        TransferChannelData.getInstance().removeTransferChannel(ctx.channel());
    }
}
