package com.fnet.common.authCenter;


import org.springframework.util.DigestUtils;

import java.util.Arrays;

/**
 * @author fys
 */
public class AuthToken {

    private static final long EXPIRE_TIME_INTERVAL = 60 * 1000;

    private final byte[] token;
    private final long createTime;

    public AuthToken(long createTime, byte[] token) {
        this.createTime = createTime;
        this.token = token.clone();
    }

    public static AuthToken createToken(long createTime, byte[] baseInfo) {
        byte[] token = DigestUtils.md5Digest(baseInfo);
        return new AuthToken(createTime, token);
    }

    public byte[] getToken() {
        return token.clone();
    }

    public boolean isExpired() {
        return createTime > System.currentTimeMillis() + EXPIRE_TIME_INTERVAL;
    }

    public boolean match(AuthToken authToken) {
        return Arrays.equals(token, authToken.token);
    }
}
