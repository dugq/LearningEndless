package com.example.connection;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.SingleThreadEventExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

/**
 * Created by dugq on 2024/4/12.
 */
public class ConnectionManger extends SingleThreadEventExecutor {

    private final ConcurrentHashMap<Long, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>(2048);

    protected ConnectionManger(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
        super(parent, threadFactory, addTaskWakesUp);
    }

    public void addChannel(long userId, ChannelHandlerContext channel) {
        channelMap.put(userId, channel);
    }

    public ChannelHandlerContext getChannel(long userId) {
        return channelMap.get(userId);
    }

    public void removeChannel(long userId) {
        channelMap.remove(userId);
    }

    public void sendAll(String message){
        byte[] bytes = message.getBytes();
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(bytes.length);
        byteBuf.writeBytes(bytes);
        sendAll(byteBuf);
    }

    public void sendAll(ByteBuf byteBuf){
        channelMap.forEach((userId, channel) -> {
            byteBuf.retain();
            TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(byteBuf.slice());
            ChannelFuture channelFuture = channel.writeAndFlush(textWebSocketFrame);
            channelFuture.addListener(future -> System.out.println("ThreadId: "+Thread.currentThread().getName()
                    + " textWebSocketFrame ref cnt: " +textWebSocketFrame.refCnt()
                    +" byte buf ref cnt: "+byteBuf.refCnt()));
        });
    }

    @Override
    protected void run() {
        for (;;) {
            Runnable task = takeTask();
            if (task != null) {
                task.run();
                updateLastExecutionTime();
            }

            if (confirmShutdown()) {
                break;
            }
        }
    }
}
