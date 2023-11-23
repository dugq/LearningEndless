package com.dugq;

import lombok.Data;
import org.apache.rocketmq.client.producer.SendResult;

import java.util.Objects;

/**
 * Created by dugq on 2023/10/27.
 */
@Data
public class MqResult {

    private String status;

    private String msgId;


    public static MqResult of(SendResult result){
        if (Objects.isNull(result)){
            MqResult mqResult = new MqResult();
            mqResult.setStatus("client exception");
            return mqResult;
        }
        MqResult mqResult = new MqResult();
        mqResult.setStatus(result.getSendStatus().name());
        mqResult.setMsgId(result.getMsgId());
        return mqResult;
    }
}
