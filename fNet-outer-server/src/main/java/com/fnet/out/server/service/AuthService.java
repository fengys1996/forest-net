package com.fnet.out.server.service;

import com.fnet.common.config.Config;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
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

    private static String remoteIP;
    private static final Object REMOTE_IP_LOCK = new Object();

    public boolean registerAuth(Message message, InetSocketAddress inetSocketAddress) {
        boolean isInSameRemoteHost;
        boolean isRegsiterMessage;
        String remoteTmpIp;

        remoteTmpIp = inetSocketAddress.getAddress().getHostAddress();

        log.debug("inner server try connect, ip: " + remoteTmpIp);
        synchronized (REMOTE_IP_LOCK) {
            isInSameRemoteHost = remoteIP == null || remoteIP.equals(remoteTmpIp);
            isRegsiterMessage = message.getType() == MessageType.REGISTER;

            if (isInSameRemoteHost && isRegsiterMessage) {
                byte[] data = message.getData();
                if (data != null) {
                    String password = new String(data);
                    if (Config.PASSWORD.equals(password)) {
                        log.info("inner server auth success,ip: " + remoteTmpIp);
                        remoteIP = remoteTmpIp;
                        return true;
                    }
                }
            }
        }
        log.info("inner server auth failed,ip: " + remoteTmpIp);
        return false;
    }

    public void clearRegisterAuthInfo() {
        synchronized (REMOTE_IP_LOCK) {
            remoteIP = null;
        }
    }
}
