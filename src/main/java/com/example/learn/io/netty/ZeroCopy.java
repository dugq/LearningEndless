package com.example.learn.io.netty;

import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by dugq on 2023/7/4.
 */
public class ZeroCopy {

    /**
     * netty的零copy其实也是应用层零copy的一种解决方案。核心解决的问题是：
     * <li/> 1、jvm缓冲区 - jvm直接内存区域的复制
     * <li/> 2、对象在堆中的复制导致数据存在多份
     * 具体实现包括：
     * <li/> CompositeByteBuf 将多个buf组合成一个buf，就解决了channel write时只能传入一个buf，所以必须合并buf而带来的复制损耗
     * <li/> slice & duplicate 浅copy解决 在多线程读同一个buf而必须复制buf而产生的额外损耗
     * <li/> netty socketChannel write 都是使用directBuf，以解决缓冲区copy 到直接内存的损耗
     * <li/> 还有个类似fileChannel的transferTo功能的方法。真正意义上的从读到写的零copy
     */
    public static void main(String[] args) {

    }

    public void compositeByteBufApi(){
        CompositeByteBuf byteBufs = Unpooled.compositeBuffer();

    }

}
