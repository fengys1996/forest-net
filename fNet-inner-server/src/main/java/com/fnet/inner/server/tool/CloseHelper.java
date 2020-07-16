package com.fnet.inner.server.tool;

import com.fnet.common.net.TcpServer;
import com.fnet.common.service.TransferChannelService;

public class CloseHelper {
    public static void closeInnerServer() {
        new Thread(()-> {
            try {
                TransferChannelService.getInstance().clear();
                TcpServer.eventLoopGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
