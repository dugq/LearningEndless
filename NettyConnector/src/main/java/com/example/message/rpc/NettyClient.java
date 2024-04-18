package com.example.message.rpc;


import com.dugq.agreement.ConnectAgreement;
import com.dugq.agreement.MessageCenterAgreement;
import com.example.connection.BeanConfig;
import com.example.connection.ConnectionMangers;
import com.example.connection.Connector;
import com.example.message.MessageApi;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Created by dugq on 2024/4/16.
 */
@Configuration
@ConditionalOnBean({BeanConfig.class, Connector.class,MessageApi.class})
@Slf4j
public class NettyClient{
    @Resource
    private BeanConfig beanConfig;

    private volatile Channel chanel;

    @Resource
    private ConnectionMangers connectionMangers;

    @Resource
    private MessageApi messageApi;

    private boolean close = false;

    private volatile int connectionRetryTime = 0;

    private volatile int connectionRetryInterval = 10;

    @PreDestroy
    public void destroy(){
        close = true;
        if (chanel!=null){
            chanel.close();
        }
        chanel.close();
    }

    public void connect(Bootstrap bootstrap) throws URISyntaxException, InterruptedException {
        String messageCenterUrl = beanConfig.getMessageCenterUrl();
        URI url = new URI(messageCenterUrl);
        ChannelFuture connect = bootstrap.connect(url.getHost(), url.getPort());
        Channel connection = connect.channel();
        // 检测连接状态
        connect.addListener((future)->{
            if (future.isSuccess()){
                upgradeAgreement(connection);
            }else{
                log.error("connection to message center error : ",future.cause());
                connection.close();
            }
        });
        // 配置连接重试
        connection.closeFuture().addListener( future -> {
            log.warn("message center connect closed！");
            // 将连接标记为不可用
            chanel = null;
            reConnection(bootstrap);
        });
    }

    //连接成功后，使用Http协议告知目标我们使用的协议
    private void upgradeAgreement(Channel connection) {
        connection.writeAndFlush(new ConnectAgreement("/myAgreement")).addListener(future -> {
            if(!future.isSuccess()){
                log.error("change http to my agreement error : ",future.cause());
                // 关闭连接重试
                connection.close();
            }else{
                // 成功了，就可以使用channel了
                chanel = connection;
                // 重置重试次数
                connectionRetryTime = 0;
            }
        });
    }

    private void reConnection(Bootstrap bootstrap) throws InterruptedException, URISyntaxException {
        // springBoot服务下线
        if(close){
            return;
        }
        // 重试3次
        if (connectionRetryTime>2){
            log.error("messageCenter挂了");
            return;
        }
        Thread.sleep(connectionRetryInterval);
        connectionRetryInterval += 1000*connectionRetryTime;
        connectionRetryTime++;
        connect(bootstrap);
    }


    @Bean
    public Bootstrap getBootstrap() throws URISyntaxException, InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        MessageSendHandle messageSendHandle = messageSendHandle();
        bootstrap
                .group(new NioEventLoopGroup(1))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(65536))
                                // 我们不希望发送大文件
                                //.addLast(new ChunkedWriteHandler())
                                .addLast(new MessageCenterAgreementHandler(messageSendHandle));
                    }
                });
        connect(bootstrap);
        return bootstrap;
    }

    @Bean
    public MessageSendHandle messageSendHandle() {
        return new MessageSendHandle(connectionMangers);
    }

    public void sendMessage(MessageCenterAgreement message, GenericFutureListener promise) {
        if (chanel==null){
            try {
                if (Objects.isNull(promise)){
                    return;
                }
                promise.operationComplete(new FailedFuture<>(null, new UnknownError()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            ChannelFuture channelFuture = chanel.writeAndFlush(message);
            if (promise!=null){
                channelFuture.addListener(promise);
            }
        }
    }
}
