package com.fnet.out.server.handler;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.out.server.data.OuterChannelData;
import com.fnet.out.server.data.Sender;
import io.netty.channel.*;

public class MonitorBrowserHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("browser is a socket connect!" + "The hashcode is " + ctx.channel().hashCode());
        OuterChannelData.getInstance().addToOuterChannelMap(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] bytes;
        int outerChannelId;

        bytes = (byte[])msg;
        outerChannelId = ctx.channel().hashCode();

        Message message = new Message(MessageType.TRANSFER_DATA, outerChannelId, bytes);
        Sender.sendMessageToTransferChannel(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("browser is socket disconnect!" + "The hashcode is = " + ctx.channel().hashCode());
        OuterChannelData.getInstance().removeOuterChannel(ctx.channel());
    }
}
