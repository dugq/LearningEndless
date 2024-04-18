package com.dugq.agreement;

import lombok.Getter;

/**
 * Created by dugq on 2024/4/16.
 */
@Getter
public class ReplayAgreement extends MessageCenterAgreement{

    // 2bytes
    private final int messageId;

    // 1byte
    private final boolean success;

    public ReplayAgreement(int messageId,boolean success) {
        super(REPLAY);
        this.messageId = messageId;
        this.success = success;
    }

}
