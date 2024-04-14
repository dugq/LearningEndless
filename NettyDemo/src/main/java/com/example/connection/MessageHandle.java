package com.example.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.annotation.Resource;

/**
 * Created by dugq on 2024/4/12.
 */
@ChannelHandler.Sharable
public class MessageHandle extends ChannelInboundHandlerAdapter {
    @Resource
    private ConnectionMangers connectionMangers;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MyAgreement){
            MyAgreement myAgreement = (MyAgreement)msg;
            long start = System.currentTimeMillis();
            ByteBuf messageOnly = myAgreement.getMessageOnly();
            for(int i = 0; i< 500;i++){
                connectionMangers.sendAll(messageOnly);
            }
            System.out.println("ThreadId: "+Thread.currentThread().getName() + " 耗时："+(System.currentTimeMillis()-start)+"  cnt = "+messageOnly.refCnt());
        }


    }
}
