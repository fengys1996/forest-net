package com.fnet.common.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class MyLengthFieldBasedFrameDecoder extends LengthFieldBasedFrameDecoder {

    public MyLengthFieldBasedFrameDecoder() {
        super(2 * 1024 * 1024, 5, 4);
    }
}
