package com.fnet.out.server.messageResolver;

import com.fnet.common.config.Config;
import com.fnet.common.net.TcpServer;
import com.fnet.common.transferProtocol.Message;
import com.fnet.common.transferProtocol.MessageResolver;
import com.fnet.common.transferProtocol.MessageType;
import com.fnet.out.server.handler.MonitorBrowserHandler;
import com.fnet.out.server.service.OuterSender;
import com.fnet.out.server.tool.CloseHelper;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class RegisterResolver implements MessageResolver {

    @Override
    public void resolve(Message message) {
        byte[] data = message.getData();
        if (data != null) {
            String password = new String(data);
            if (Config.PASSWORD.equals(password)) {
                OuterSender.getInstance().sendRegisterResponseMessage(true);
                startMonitorBrowser();
                return;
            }
        }
        OuterSender.getInstance().sendRegisterResponseMessage(false);
        CloseHelper.clearData();
    }

    @Override
    public boolean isSupport(Message message) {
        return message.getType() == MessageType.REGISTER;
    }

    private void startMonitorBrowser() {
        new Thread(() -> {
            try {
                new TcpServer().startMonitor(Config.OUTER_REMOTE_PORT, new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ByteArrayEncoder(), new ByteArrayDecoder(), new MonitorBrowserHandler());
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
