package com.example.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

/**
 * Created by dugq on 2024/4/12.
 */
@Slf4j
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


    public void sendAll(ByteBuf byteBuf){
        try {
            channelMap.forEach((userId, channel) -> {
                byteBuf.retain();
                ChannelFuture channelFuture = channel.writeAndFlush(byteBuf);
                channelFuture.addListener(future -> log.info("ThreadId: {} byte buf ref cnt: {}",Thread.currentThread().getName(),byteBuf.refCnt()));
            });
        }finally {
            byteBuf.release();
        }

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
