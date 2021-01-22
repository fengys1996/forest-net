package com.fnet.common.authCenter;

import com.fnet.common.transfer.protocol.Message;

import java.net.InetSocketAddress;

public interface Authencator {

    void setPassword(String password);

    boolean registerAuth(Message message, InetSocketAddress inetSocketAddress);
}
