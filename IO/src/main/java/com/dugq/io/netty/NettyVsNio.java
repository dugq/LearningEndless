package com.dugq.io.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by dugq on 2023/6/20.
 */
public class NettyVsNio {

    public static void main(String[] args) {
        Channel channel;
        channel = new NioSocketChannel();
        channel = new NioServerSocketChannel();

//        ChannelPipeline channelPipeline = new DefaultChannelPipeline(channel);
//        ChannelHandlerContext context = new DefaultChannelHandlerContext();


        ChannelHandler channelHandler;
        channelHandler = new ChannelInboundHandlerAdapter();
        channelHandler = new ChannelOutboundHandlerAdapter();



    }

}
