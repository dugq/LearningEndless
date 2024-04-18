package com.dugq.message;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by dugq on 2024/4/16.
 */
@Getter
@Setter
public class ByteMessage extends MessageContent{

    private ByteBuf content;


    @Override
    public int getContentSize() {
        return content.readableBytes()+2;
    }

    @Override
    public void writeContent(ByteBuf buf) {
        try {
            buf.writeShort(content.readableBytes());
            buf.writeBytes(content);
        }finally {
            content.release();
        }
    }

    @Override
    public void release() {
        content.release();
    }
}
