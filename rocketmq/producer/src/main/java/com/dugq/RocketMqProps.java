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
    private String commonTopic;

    @Data
    public static class ProducerConfig{
        private String nameServerHost;
        private String groupName;
    }
}
