package com.example.message.rpc;

import com.dugq.agreement.MessageAgreement;
import com.dugq.message.ByteMessage;
import com.dugq.message.MessageContent;
import com.example.connection.ConnectionMangers;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by dugq on 2024/4/16.
 */
@ChannelHandler.Sharable
public class MessageSendHandle extends ChannelInboundHandlerAdapter {


    private final ConnectionMangers connectionMangers;

    public MessageSendHandle(ConnectionMangers connectionMangers) {
        this.connectionMangers = connectionMangers;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MessageAgreement){
            MessageAgreement agreement = (MessageAgreement)msg;
            for (MessageContent messageContent : agreement.getMessageContents()) {
                ByteMessage byteMessage = (ByteMessage)messageContent;
                connectionMangers.sendAll(byteMessage);
            }
        }else{
            super.channelRead(ctx, msg);
        }

    }
}
