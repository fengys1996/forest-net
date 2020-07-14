package com.fnet.out.server.messageResolver;

import com.fnet.common.net.TcpServer;
import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.out.server.handler.MonitorBrowserHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class RegisterResolver implements MessageResolver {

    @Override
    public void resolve(Message message) throws InterruptedException {
        // first authentication
        System.out.println("[resolver] register!");
        // second monitor browser
        new TcpServer().startMonitor(8081, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ByteArrayEncoder(), new ByteArrayDecoder(), new MonitorBrowserHandler());
            }
        });
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.REGISTER;
    }
}
