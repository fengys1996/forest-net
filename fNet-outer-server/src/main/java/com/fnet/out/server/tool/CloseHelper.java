package com.fnet.out.server.tool;

import com.fnet.common.service.TransferChannelService;

public class CloseHelper {
    public static void closeOuterServer() {
        TransferChannelService.getInstance().clearAllChannel();
    }
}
