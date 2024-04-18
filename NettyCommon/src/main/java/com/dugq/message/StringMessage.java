package com.dugq.message;

import io.netty.buffer.ByteBuf;

/**
 * Created by dugq on 2024/4/16.
 */
public class StringMessage extends MessageContent{
    private String content;
    @Override
    public int getContentSize() {
        return content.length();
    }

    @Override
    public void writeContent(ByteBuf buf) {
        for (int i = 0; i<content.length(); i++) {
            buf.writeByte((byte)content.charAt(i));
        }
    }
}
