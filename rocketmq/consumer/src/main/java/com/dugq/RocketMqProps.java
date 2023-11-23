package com.dugq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dugq on 2023/10/27.
 */
@ConfigurationProperties(prefix = "rocketmq")
@Data
@Configuration
public class RocketMqProps {
    private ProducerConfig producer;
    private ConsumerConfig consumer;
    private String commonTopic;
    private String nameServerHost;

    @Data
    public static class ProducerConfig{
        private String groupName;
    }

    @Data
    public static class ConsumerConfig{
        private String groupName;
    }
}
