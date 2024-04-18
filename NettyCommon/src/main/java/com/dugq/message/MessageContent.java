package com.dugq.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dugq on 2024/4/15.
 */
@Getter
@Setter
@Slf4j
public abstract class MessageContent {

    private byte version = (byte)1;

    // 2byte 65535
    private int messageId;

    // 0 : one Person 1: send all
    private byte type;

    //发送人数 2 字节 65535最大值
    private int userSize;

    //用户id集合 每个占8字节
    private List<Long> userId;

    public static ByteMessage checkFromRequest(ByteBuf message){
        //todo decode
        ByteMessage messageContent = new ByteMessage();
        messageContent.setContent(message);
        return messageContent;
    }

    public ByteBuf toBuf(ByteBufAllocator alloc){
        int size = getLength();
        ByteBuf buf = alloc.buffer(size);
        buf.writeByte(version);
        buf.writeShort(messageId);
        buf.writeByte(type);
        buf.writeShort(userSize);
        for (int i = 0; i < userSize; i++) {
            buf.writeLong(userId.get(i));
        }
        writeContent(buf);
        return buf;
    }

    public static ByteMessage readFromBuff(ByteBuf buffer){
        ByteMessage message = new ByteMessage();
        message.setVersion(buffer.readByte());
        message.setMessageId(buffer.readShort());
        message.setType(buffer.readByte());
        message.setUserSize(buffer.readShort());
        List<Long> userIds = new ArrayList<>();
        for(int i = 0; i<message.getUserSize();i++){
            long userId = buffer.readLong();
            userIds.add(userId);
        }
        message.setUserId(userIds);
        int size = buffer.readShort();
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(size,size);
        try {
            byteBuf.writeBytes(buffer,size);
            message.setContent(byteBuf);
        }catch (Exception e){
            log.error("Failed to write",e);
            byteBuf.release();
        }
        return message;
    }

    protected abstract int getContentSize();

    protected abstract void writeContent(ByteBuf buf);

    public int getLength() {
        int size = getContentSize();
        size+=6;
        size+=userSize*8;
        return size;
    }

    public void release(){

    }
}
