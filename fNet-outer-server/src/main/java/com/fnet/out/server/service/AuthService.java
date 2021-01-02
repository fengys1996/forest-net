package com.fnet.out.server.service;

import com.fnet.common.config.Config;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;

import java.net.InetSocketAddress;

public class AuthService {

    private static volatile AuthService authService;

    private AuthService() {

    }

    public static AuthService getInstance() {
        if (authService == null) {
            synchronized (AuthService.class) {
                if (authService == null) {
                    authService = new AuthService();
                }
            }
        }
        return authService;
    }

    String remoteIP;

    public boolean registerAuth(Message message, InetSocketAddress inetSocketAddress) {
        boolean isInSameRemoteHost;
        boolean isRegsiterMessage;
        String remoteTmpIP;

        remoteTmpIP = inetSocketAddress.getAddress().getHostAddress();
        isInSameRemoteHost = this.remoteIP == null || this.remoteIP.equals(remoteTmpIP);
        isRegsiterMessage = message.getType() == MessageType.REGISTER;

        if (isInSameRemoteHost && isRegsiterMessage) {
            byte[] data = message.getData();
            if (data != null) {
                String password = new String(data);
                if (Config.PASSWORD.equals(password)) {
                    this.remoteIP = remoteTmpIP;
                    return true;
                }
            }
        }
        return false;
    }

    public void clearRegisterAuthInfo() {
        remoteIP = null;
    }
}
