package com.fnet.out.server.config;

import com.fnet.common.service.Sender;
import com.fnet.out.server.service.OuterSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public Sender sender() {
        OuterSender outerSender = new OuterSender();
        outerSender.setMultiTransfer();
        return outerSender;
    }
}
