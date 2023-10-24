package com.dugq.io.buffer;

import java.nio.ByteBuffer;

public class DirectBufferTest {

    /**
     * {@link java.nio.DirectByteBuffer}
     */
    public void DirectBuff(){
        //directBuff  本类是隐藏的。只能通过这种方式获取
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

    }

}
