package com.fnet.out.server.authCenter;

import com.fnet.common.transfer.protocol.Message;

import java.net.InetSocketAddress;

public interface AuthService {

    boolean registerAuth(Message message, InetSocketAddress inetSocketAddress);
}
