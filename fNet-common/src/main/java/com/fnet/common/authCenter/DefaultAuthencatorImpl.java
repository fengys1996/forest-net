package com.fnet.common.authCenter;

import com.fnet.common.transfer.protocol.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
public class DefaultAuthencatorImpl implements Authencator {

    String configOfPasword;

    @Override
    public void setPassword(String password) {
        this.configOfPasword = password;
    }

    @Override
    public boolean registerAuth(Message message, InetSocketAddress inetSocketAddress) {

        AuthMessage authMessage = AuthMessage.buildMessage(message.getPayLoad());
        if (authMessage == null)    return false;

        long timestamp = authMessage.getTimestamp();
        AuthToken authTokenFromClient = new AuthToken(timestamp, authMessage.getToken());

        if (authTokenFromClient.isExpired())     return false;

        String baseInfo = timestamp + configOfPasword;
        AuthToken authTokenGenrateByServer = AuthToken.createToken(authMessage.getTimestamp(), baseInfo.getBytes());
        if (!authTokenGenrateByServer.match(authTokenFromClient))   return false;
        return true;
    }
}
