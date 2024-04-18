package com.dugq.agreement;

import lombok.Getter;

/**
 * Created by dugq on 2024/4/16.
 */
@Getter
public class ReceiveAgreement extends MessageCenterAgreement{

    private int messageId;
    private int count;
    public ReceiveAgreement(int messageId,int count) {
        super(RECEIVE);
        this.messageId = messageId;
        this.count = count;
    }
}
