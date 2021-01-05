package com.fnet.common.handler;

import com.fnet.common.config.Config;
import com.fnet.common.service.AbstractSender;
import com.fnet.common.service.Sender;
import com.fnet.common.transfer.Transfer;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageResolver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class AbstractMonitorHandler extends ChannelInboundHandlerAdapter {

    public Sender sender;
    MessageResolver resolver;

    public AbstractMonitorHandler(Sender sender, MessageResolver resolver) {
        this.sender = sender;
        this.resolver = resolver;
    }

    protected int numsOfActiveChannel = 0;
    protected final Object LOCK = new Object();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Transfer channel connect!");
        Transfer transfer = ((AbstractSender) sender).getTransfer();
        transfer.addTransferChannel(ctx.channel());

        synchronized (LOCK) {
            if (++numsOfActiveChannel == Config.TRANSFER_CHANNEL_NUMBERS) {
                log.info("All transfer channel connect!");
                doSomethingAfterAllTransferChannelActive();
            }
        }
        super.channelActive(ctx);
    }

    public void doSomethingOnChannelActive() {
        // defalut do nothing
    }

    public void doSomethingAfterAllTransferChannelActive() {
        // defalut do nothing
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        resolver.resolve((Message) msg, sender);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Transfer channel disconnect!");
        Channel channel = ctx.channel();
        ((AbstractSender) sender).getTransfer().removeTransferChannel(channel);
        channel.close();

        synchronized (LOCK) {
            if (--numsOfActiveChannel == 0) {
                log.info("All transfer channel disconnect!");
                doSomethingAfterAllTransferChannelInactive();
            }
        }
    }

    public void doSomethingOnChannelInactive() {
        // defalut do nothing
    }

    public void doSomethingAfterAllTransferChannelInactive() {
        // defalut do nothing
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (cause instanceof IOException) {
            if ("远程主机强迫关闭了一个现有的连接。".equals(cause.getMessage())) {
                log.info("远程主机强迫关闭了一个现有的连接。");
                return;
            }
        }
        ctx.fireExceptionCaught(cause);
    }
}
