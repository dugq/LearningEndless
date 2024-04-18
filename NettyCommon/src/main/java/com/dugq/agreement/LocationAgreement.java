package com.dugq.agreement;

import lombok.Getter;

import java.util.List;

/**
 * Created by dugq on 2024/4/16.
 */
@Getter
public class LocationAgreement extends MessageCenterAgreement{


    private List<Integer> messageIds;

    private String location;
    public LocationAgreement(List<Integer> messageIds , String location) {
        super(LOCATION);
        this.messageIds = messageIds;
        this.location = location;
    }

}
