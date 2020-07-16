package com.fnet.out.server.tool;

import com.fnet.common.service.TransferChannelService;
import com.fnet.out.server.service.OuterChannelDataService;

public class CloseHelper {
    public static void clearData() {
        TransferChannelService.getInstance().clear();
        OuterChannelDataService.getInstance().clear();
    }
}
