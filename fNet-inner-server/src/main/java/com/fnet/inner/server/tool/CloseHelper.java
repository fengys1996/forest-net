package com.fnet.inner.server.tool;

import com.fnet.common.net.TcpServer;
import com.fnet.inner.server.service.InnerSender;

public class CloseHelper {
    public static void closeInnerServer() {
        new Thread(()-> {
            try {
                InnerSender.getInstance().getTransfer().free();
                TcpServer.eventLoopGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
