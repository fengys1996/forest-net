package com.fnet.server.handler;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.server.Data.OuterChannelData;
import com.fnet.server.Data.Sender;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class MonitorBrowserHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        OuterChannelData.getInstance().addToOuterChannelMap(channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            byte[] bytes;
            int outerChannelId;

            bytes = (byte[])msg;
            outerChannelId = ctx.channel().hashCode();
            Message message = new Message(MessageType.TRANSFER_DATA, outerChannelId, bytes);
            Sender.sendMessageToTransferChannel(message);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        OuterChannelData.getInstance().removeOuterChannel(ctx.channel());
    }
}
