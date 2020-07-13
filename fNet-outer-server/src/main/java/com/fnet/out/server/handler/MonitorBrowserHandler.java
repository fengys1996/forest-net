package com.fnet.out.server.handler;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.out.server.data.OuterChannelData;
import com.fnet.out.server.data.Sender;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;

public class MonitorBrowserHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("browser is a socket connect!" + "channel hashcode = " + ctx.channel().hashCode());
        Channel channel = ctx.channel();
        OuterChannelData.getInstance().addToOuterChannelMap(channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] bytes;
        int outerChannelId;

        bytes = (byte[])msg;

        System.out.print(new String(bytes));

        outerChannelId = ctx.channel().hashCode();
        Message message = new Message(MessageType.TRANSFER_DATA, outerChannelId, bytes);
        //Sender.sendMessageToTransferChannel(message);
        ctx.writeAndFlush("1233123213".getBytes()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("complete=" + future.isSuccess());
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        OuterChannelData.getInstance().removeOuterChannel(ctx.channel());
    }
}
