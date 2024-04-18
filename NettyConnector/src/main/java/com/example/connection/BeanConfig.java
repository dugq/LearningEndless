package com.example.connection;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dugq on 2024/4/12.
 */
@Configuration
@Getter
public class BeanConfig {
    @Value("${server.port}")
    private int port;

    @Value("${server.thread.num}")
    private int threadNum;

    @Value("${message.center.url}")
    private String messageCenterUrl;


}
