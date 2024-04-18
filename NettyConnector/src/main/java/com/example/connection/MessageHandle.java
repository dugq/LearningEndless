package com.example.connection;

import com.example.message.MessageApi;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * Created by dugq on 2024/4/12.
 */
@ChannelHandler.Sharable
@Slf4j
public class MessageHandle extends ChannelInboundHandlerAdapter {
    @Resource
    private ConnectionMangers connectionMangers;
    @Resource
    private MessageApi messageApi;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MyAgreement){
            MyAgreement myAgreement = (MyAgreement)msg;
            if (myAgreement.getInstruct() == MyAgreement.MyInstruct.MESSAGE){
                ByteBuf messageOnly = myAgreement.getMessageOnly();
                messageApi.byteMessage(messageOnly);
                return;
            }
            if (myAgreement.getInstruct() == MyAgreement.MyInstruct.REPLY_MESSAGE){
                // utf-8编码
                ByteBuf message = myAgreement.getMessage();
                int messageId = 0;
                while(message.readableBytes()>0){
                    messageId = messageId*10 + (message.readByte()-'0');
                }
                //优化聚合处理
                messageApi.sendSuccess(messageId,1);
                return;
            }
        }
        super.channelRead(ctx,msg);

    }
}
