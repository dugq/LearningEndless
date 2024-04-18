package com.dugq.messagecenter.server.handler.message;

import com.dugq.agreement.ReceiveAgreement;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by dugq on 2024/4/15.
 */
@ChannelHandler.Sharable
public class ReplayHandler extends ChannelInboundHandlerAdapter {

    private MessageQueue messageQueue;

    public ReplayHandler(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof ReceiveAgreement){
            ReceiveAgreement agreement = (ReceiveAgreement)msg;
            messageQueue.updateResult(agreement.getMessageId(),agreement.getCount());
        }else{
            super.channelRead(ctx, msg);
        }
    }
}
