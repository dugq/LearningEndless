package com.dugq.agreement;

import com.dugq.message.ByteMessage;
import com.dugq.message.MessageContent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dugq on 2024/4/15.
 */
@Slf4j
public class AgreementDecoder extends LengthFieldBasedFrameDecoder {

    public AgreementDecoder() {
        super(2048, 0, 2);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode==null) {
            return null;
        }
        if(decode instanceof ByteBuf){
            ByteBuf content = (ByteBuf) decode;
            MessageCenterAgreement agreement = null;
            try {
                // 跳过长度字段
                content.readByte();
                content.readByte();
                byte op = content.readByte();
                if(op == MessageCenterAgreement.MESSAGE_SEND){
                    List<MessageContent> list = new ArrayList<>();
                    // 先构建再解析，这样当解析失败时，也能释放内存
                    agreement = new MessageAgreement(list);
                    while(content.readableBytes()>0){
                        ByteMessage byteMessage = MessageContent.readFromBuff(content);
                        list.add(byteMessage);
                    }
                }else if(op == MessageCenterAgreement.REPLAY){
                    agreement = new ReplayAgreement(content.readShort(),content.readBoolean());
                }else if(op == MessageCenterAgreement.LOCATION){
                    int size = content.readShort();
                    List<Integer> messageIds = new ArrayList<>();
                    for(int i = 0; i< size;i++){
                        int messageId = content.readShort();
                        messageIds.add(messageId);
                    }
                    int length = content.readableBytes();
                    byte[] bytes = new byte[length];
                    content.readBytes(bytes);
                    agreement = new LocationAgreement(messageIds,new String(bytes));
                }else if(op == MessageCenterAgreement.RECEIVE){
                    agreement = new ReceiveAgreement(content.readShort(),content.readShort());
                }else {
                    return decode;
                }
                return agreement;
            }catch (Exception e){
                if (agreement!=null){
                    agreement.release();
                }
                log.error("解析协议错误",e);
                return decode;
            }finally {
                content.release();
            }
        }
        return decode;
    }

}

