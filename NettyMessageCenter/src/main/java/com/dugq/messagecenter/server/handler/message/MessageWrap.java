package com.dugq.messagecenter.server.handler.message;

import com.dugq.message.MessageContent;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by dugq on 2024/4/16.
 */
@Setter
@Getter
public class MessageWrap {

    private MessageContent messageContent;

    //接收到的人数
    private int receivedNum;

    //目前处于位置
    private String location;

    // 发消息的时间
    private Long timeStamp;

    public MessageWrap(MessageContent messageContent) {
        this.messageContent = messageContent;
        this.timeStamp = System.currentTimeMillis();
    }

    public void increaseReceivedNum(int increment) {
        this.receivedNum+=increment;
    }
}
