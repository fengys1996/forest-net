package com.fnet.inner.server.handler;

import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.inner.server.data.ContactOfOuterToInnerChannel;
import com.fnet.inner.server.data.Sender;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MonitorRealServerHandler extends ChannelInboundHandlerAdapter {

    private Message message;

    public MonitorRealServerHandler(Message message) {
        this.message = message;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("add!!!inner channel hashcode=" + ctx.channel().hashCode() + "...outchannel id:" + message.getOuterChannelId());
        ContactOfOuterToInnerChannel.getInstance().addToMap(message.getOuterChannelId(), ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("accept message from real server!");
        Sender.sendMessageToOuterServer(new Message(MessageType.TRANSFER_DATA, message.getOuterChannelId(), (byte[])msg));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("real channel in active!");
        ContactOfOuterToInnerChannel.getInstance().removeFromMap(message.getOuterChannelId());
    }
}
