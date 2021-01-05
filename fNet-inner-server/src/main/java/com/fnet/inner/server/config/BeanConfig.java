package com.fnet.inner.server.config;

import com.fnet.common.service.Sender;
import com.fnet.inner.server.service.InnerSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public Sender sender() {
        InnerSender innerSender;
        innerSender = new InnerSender();
        innerSender.setMultiTransfer();
        return innerSender;
    }
}
