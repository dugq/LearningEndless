package com.example.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dugq on 2024/4/14.
 */
public class ConnectionMangers extends DefaultEventExecutorGroup {

    private ThreadLocal<ConnectionManger> connectionMangerCache = new ThreadLocal<>();
    private final int nThreads ;

    public ConnectionMangers(int nThreads) {
        super(nThreads);
        this.nThreads = nThreads;
    }

    public void addChannel(long userId, ChannelHandlerContext userChannel) {
        ConnectionManger connectionManger = getConnectionManger();
        connectionManger.addChannel(userId, userChannel);
        execute(() -> removeDuplicate(userId,userChannel,connectionManger));
    }

    private void removeDuplicate(long userId,ChannelHandlerContext userChannel,ConnectionManger useConnectionManger){
        Iterator<EventExecutor> iterator = iterator();
        while (iterator.hasNext()) {
            for (EventExecutor eventExecutor : iterator.next()) {
                ConnectionManger connectionManger = (ConnectionManger) eventExecutor;
                if (connectionManger == useConnectionManger){
                    continue;
                }
                ChannelHandlerContext channel = connectionManger.getChannel(userId);
                if(channel!=null){
                    connectionManger.removeChannel(userId);
                    if(userChannel != channel){
                        channel.close();
                    }
                }
            }
        }
    }

    public ChannelHandlerContext getChannel(long userId) {
        Iterator<EventExecutor> iterator = iterator();
        while (iterator.hasNext()) {
            for (EventExecutor eventExecutor : iterator.next()) {
                ChannelHandlerContext channel = ((ConnectionManger) eventExecutor).getChannel(userId);
                if(Objects.nonNull(channel)){
                    return channel;
                }
            }
        }
        return null;

    }

    @Override
    protected EventExecutor newChild(Executor executor, Object... args) throws Exception {
        return new ConnectionManger(this, new MyThreadFactory(),true);
    }

    public void removeChannel(Long userId, Channel channel) {
        ConnectionManger connectionManger = getConnectionManger();
        ChannelHandlerContext channelHandlerContext = connectionManger.getChannel(userId);
        if(channelHandlerContext==null) {
            return;
        }
        if(channelHandlerContext.channel() == channel){
            connectionManger.removeChannel(userId);
        }
    }

    private static  class  MyThreadFactory implements ThreadFactory {
        private static final AtomicInteger count = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("ConnectorManger-" + count.getAndIncrement());
            return thread;
        }
    }

    private ConnectionManger getConnectionManger(){
        ConnectionManger connectionManger = connectionMangerCache.get();
        if(connectionManger != null){
            return connectionManger;
        }
        connectionManger = (ConnectionManger)super.next();
        connectionMangerCache.set(connectionManger);
        return connectionManger;
    }

    public void sendAll(String message){
        Iterator<EventExecutor> iterator = iterator();
        CountDownLatch countDownLatch  = new CountDownLatch(nThreads);
        long start = System.currentTimeMillis();
        while (iterator.hasNext()) {
            for (EventExecutor eventExecutor : iterator.next()) {
                ConnectionManger connectionManger = (ConnectionManger) eventExecutor;
                connectionManger.execute(()->{
                    connectionManger.sendAll(message);
                    countDownLatch.countDown();
                });
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("sendAll cost time:"+(System.currentTimeMillis()-start));
    }

    public void sendAll(ByteBuf byteBuf){
        Iterator<EventExecutor> iterator = iterator();
        CountDownLatch countDownLatch  = new CountDownLatch(nThreads);
        while (iterator.hasNext()) {
            for (EventExecutor eventExecutor : iterator.next()) {
                ConnectionManger connectionManger = (ConnectionManger) eventExecutor;
                connectionManger.execute(()->{

                    connectionManger.sendAll(byteBuf);
                    countDownLatch.countDown();
                });
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
