package com.dugq.agreement;

import com.dugq.message.MessageContent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by dugq on 2024/4/16.
 */
public class AgreementEncoder extends MessageToMessageEncoder<MessageCenterAgreement> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageCenterAgreement msg, List<Object> out) throws Exception {
        byte op = msg.getType();
        // 指令，消息体长度需要各自追加
        int length = 1;
        ByteBuf byteBuf;
        if (op == MessageCenterAgreement.LONG_CONNECT){
            byteBuf = ctx.alloc().buffer(length);
            // length
            byteBuf.writeByte(0);
            byteBuf.writeByte(length & 0xFF);
            // write op
            byteBuf.writeByte(op);
            out.add(byteBuf);
        }else if(op == MessageCenterAgreement.MESSAGE_SEND){
            MessageAgreement messageAgreement = (MessageAgreement)msg;
            List<MessageContent> messageContents = messageAgreement.getMessageContents();
            for (MessageContent messageContent : messageContents) {
                length+=messageContent.getLength();
            }
            byteBuf = ctx.alloc().buffer(3);
            // length
            byteBuf.writeByte(length >>8 & 0xFF);
            byteBuf.writeByte(length & 0xFF);
            byteBuf.writeByte(op);
            out.add(byteBuf);
            for (MessageContent messageContent : messageContents) {
                out.add(messageContent.toBuf(ctx.alloc()));
            }
        }else if(op == MessageCenterAgreement.REPLAY){
            ReplayAgreement messageAgreement = (ReplayAgreement)msg;
            length+=3;
            byteBuf =ctx.alloc().buffer(length);
            // length
            byteBuf.writeByte(0);
            byteBuf.writeByte(length & 0xFF);
            byteBuf.writeShort(messageAgreement.getMessageId());
            byteBuf.writeBoolean(messageAgreement.isSuccess());
            out.add(byteBuf);
        }else if(op == MessageCenterAgreement.LOCATION){
            LocationAgreement locationAgreement = (LocationAgreement)msg;
            //长度 = 固定长度 + 2 + 2 * messageIds.length
            length+=2 + 2 * locationAgreement.getMessageIds().size();
            String location = locationAgreement.getLocation();
            length+=location.length();
            byteBuf = ctx.alloc().buffer(length);
            // header
            byteBuf.writeByte(0);
            byteBuf.writeByte(length & 0xFF);
            byteBuf.writeByte(op);
            // write messageIds size
            byteBuf.writeShort(locationAgreement.getMessageIds().size());
            // write messageIds
            for (Integer messageId : locationAgreement.getMessageIds()) {
                byteBuf.writeShort(messageId);
            }
            // write location
            for(int i = 0; i<location.length();i++){
                byteBuf.writeByte((byte)location.charAt(i));
            }
            out.add(byteBuf);
        }else if(op == MessageCenterAgreement.RECEIVE){
            ReceiveAgreement receiveAgreement = (ReceiveAgreement)msg;
            length+=4;
            byteBuf = ctx.alloc().buffer(length);
            // header
            byteBuf.writeByte(0);
            byteBuf.writeByte(length & 0xFF);
            byteBuf.writeByte(op);
            byteBuf.writeShort(receiveAgreement.getMessageId());
            byteBuf.writeShort(receiveAgreement.getCount());
            out.add(byteBuf);
        }
    }
}
