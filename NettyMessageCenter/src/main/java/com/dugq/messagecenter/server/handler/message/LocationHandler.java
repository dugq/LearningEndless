package com.dugq.messagecenter.server.handler.message;

import com.dugq.agreement.LocationAgreement;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by dugq on 2024/4/16.
 */
@ChannelHandler.Sharable
public class LocationHandler extends ChannelInboundHandlerAdapter {

    private MessageQueue messageQueue;

    public LocationHandler(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof LocationAgreement){
            LocationAgreement agreement = (LocationAgreement)msg;
            messageQueue.location(agreement.getMessageIds(),agreement.getLocation());
        }else{
            super.channelRead(ctx, msg);
        }
    }
}
