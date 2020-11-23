package com.fnet.out.server.tool;

import com.fnet.out.server.service.OuterChannelDataService;
import com.fnet.out.server.service.OuterSender;

public class CloseHelper {
    public static void clearData() {
        OuterSender.getInstance().getTransfer().free();
        OuterChannelDataService.getInstance().clear();
    }
}
