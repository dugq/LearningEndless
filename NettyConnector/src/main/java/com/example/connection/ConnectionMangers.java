package com.example.connection;

import com.dugq.message.ByteMessage;
import com.example.message.MessageApi;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dugq on 2024/4/14.
 */
@Slf4j
public class ConnectionMangers extends DefaultEventExecutorGroup {

    private ThreadLocal<ConnectionManger> connectionMangerCache = new ThreadLocal<>();
    private final int nThreads ;

    private final MessageApi messageApi;

    public ConnectionMangers(int nThreads, MessageApi messageApi) {
        super(nThreads);
        this.nThreads = nThreads;
        this.messageApi = messageApi;
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

    // 内部自动释放byteBuf
    public void sendAll(ByteMessage byteMessage){
        this.execute(()->{
            messageApi.locationMessage(byteMessage.getMessageId());
            TextWebSocketFrame frame = new TextWebSocketFrame(byteMessage.getContent());
            ByteBuf message = null;
            try {
                message = MyWebSocketFrameEncoder.encode(frame);
                Iterator<EventExecutor> iterator = iterator();
                while (iterator.hasNext()) {
                    for (EventExecutor eventExecutor : iterator.next()) {
                        message.retain();
                        ByteBuf copy = message.slice();
                        ConnectionManger connectionManger = (ConnectionManger) eventExecutor;
                        connectionManger.execute(()-> connectionManger.sendAll(copy));
                    }
                }
            }catch (Exception e){
                log.error("build message error",e);
            }finally {
                byteMessage.getContent().release();
                if (message!=null){
                    message.release();
                }
            }
        });
    }

}
