package com.dugq.messagecenter.server.handler.message;

import com.dugq.agreement.MessageCenterAgreement;
import com.dugq.message.MessageContent;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.Getter;

/**
 * Created by dugq on 2024/4/15.
 */
@Getter
public class MessageRequest implements Comparable<MessageRequest>{

    private final MessageContent messageContent;

    private final int order;

    private DefaultPromise<Integer> defaultPromise;


    public MessageRequest(Channel channel, MessageQueue eventExecutors, int order, MessageContent messageContent) {
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);
        promise.addListener((v)->{
            MessageCenterAgreement agreement;
                if (v.isSuccess()){
                    agreement = MessageCenterAgreement.buildReplay((int) v.get(),true);
                }else{
                    agreement = MessageCenterAgreement.buildReplay((int) v.get(),true);
                }
                channel.writeAndFlush(agreement);

        });
        this.order = order;
        this.messageContent = messageContent;
    }

    @Override
    public int compareTo(MessageRequest o) {
        return this.order- o.order;

    }
}
