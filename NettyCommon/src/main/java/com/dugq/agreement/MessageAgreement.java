package com.dugq.agreement;

import com.dugq.message.MessageContent;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Created by dugq on 2024/4/16.
 */
@Getter
public class MessageAgreement extends  MessageCenterAgreement{

    private final List<MessageContent> messageContents;

    public MessageAgreement(List<MessageContent> messageContents) {
        super(MESSAGE_SEND);
        this.messageContents = messageContents;
    }

    public MessageAgreement(MessageContent content) {
        super(MESSAGE_SEND);
        this.messageContents = Collections.singletonList(content);
    }

    @Override
    public void release() {
        if (messageContents == null){
            return;
        }
        for (MessageContent messageContent : messageContents) {
            messageContent.release();
        }
    }
}
