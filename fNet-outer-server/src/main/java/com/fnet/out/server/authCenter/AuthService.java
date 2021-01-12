package com.fnet.out.server.authCenter;

import com.fnet.common.config.Config;
import com.fnet.common.transfer.protocol.Message;
import com.fnet.common.transfer.protocol.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
public class AuthService {
    public boolean registerAuth(Message message, InetSocketAddress inetSocketAddress) {
            if (message.getType() == MessageType.REGISTER) {
                byte[] data = message.getData();
                if (data != null) {
                    String password = new String(data);
                    if (Config.PASSWORD.equals(password)) {
                        log.info("inner server auth success");
                        return true;
                    }
                }
            }
        log.info("inner server auth failed");
        return false;
    }
}
