package com.fnet.server.messageResolver;

import com.fnet.common.net.TcpServer;
import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.server.handler.MonitorBrowserHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class RegisterResolver implements MessageResolver {

    @Override
    public void resolve(Message message) throws InterruptedException {
        new TcpServer().startMonitor(8081, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), new MonitorBrowserHandler());
            }
        });
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.REGISTER;
    }
}
