package com.dugq.messagecenter.server.handler.message;

import com.dugq.agreement.MessageAgreement;
import com.dugq.message.MessageContent;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Created by dugq on 2024/4/15.
 */
@ChannelHandler.Sharable
public class MessageSendHandle extends ChannelInboundHandlerAdapter {
    private final MessageQueue messageQueue;

    public MessageSendHandle(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            FullHttpRequest request = (FullHttpRequest)msg;
            MessageContent messageContent =  MessageContent.checkFromRequest(request.content());
            messageQueue.addMessage(ctx.channel(),messageContent);
        }else if (msg instanceof MessageAgreement){
            MessageAgreement agreement = (MessageAgreement) msg;
            messageQueue.addMessage(ctx.channel(),agreement.getMessageContents().get(0));
        }else{
            super.channelRead(ctx, msg);
        }
    }

}
