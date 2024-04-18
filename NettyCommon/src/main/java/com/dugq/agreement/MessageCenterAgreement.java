package com.dugq.agreement;

import com.dugq.message.MessageContent;
import lombok.Getter;

import java.util.List;

/**
 * Created by dugq on 2024/4/15.
 */
@Getter
public abstract class MessageCenterAgreement {
     static final byte LONG_CONNECT = (byte)1;
     static final byte MESSAGE_SEND = (byte)2;
     static final byte REPLAY = (byte)3;
    static final byte LOCATION = (byte)4;
    static final byte RECEIVE = (byte)5;

    private byte type;

    public MessageCenterAgreement(byte type) {
        this.type = type;
    }

    public static MessageAgreement buildMessageSend(List<MessageContent> messageList){
        return new MessageAgreement(messageList);
    }

    public static ReplayAgreement buildReplay(Integer content,boolean success){
        return new ReplayAgreement(content,success);
    }

    public void release() {

    }
}
