package com.example.connection;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * Created by dugq on 2024/4/12.
 */
@Slf4j
@Configuration
@ConditionalOnBean(value = BeanConfig.class)
public class Connector {

    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workGroup ;
    ServerBootstrap serverBootstrap;

    private ConnectionMangers connectionMangers;

    @Resource
    private BeanConfig beanConfig;


    @PreDestroy
    public void shutdown() throws InterruptedException {
        if(Objects.isNull(serverBootstrap)){
            return;
        }
        try {
            serverBootstrap.config().group().shutdownGracefully().sync();
            log.info("bossGroup shutdown success");
            serverBootstrap.config().childGroup().shutdownGracefully().sync();
            log.info("workGroup shutdown success");
            connectionMangers.shutdownGracefully().sync();
            log.info("executorGroup shutdown success");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Bean
    public MyAgreementHandler myAgreementHandler() {
        return new MyAgreementHandler();
    }

    @Bean
    public AuthHandler authHandler() {
        return new AuthHandler();
    }

    @Bean
    public MessageHandle messageHandle() {
        System.out.println("messageHandle");
        return new MessageHandle();
    }

    @Bean
    public ConnectionMangers connectionMangers() {
        return this.connectionMangers =new ConnectionMangers(beanConfig.getThreadNum());
    }

    @Bean
    public ServerBootstrap serverBootstrap() throws InterruptedException {
        serverBootstrap = new ServerBootstrap();
        this.workGroup = new NioEventLoopGroup(beanConfig.getThreadNum());
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(65536))
                                .addLast(new WebSocketServerProtocolHandler("/ws"))
                                .addLast(myAgreementHandler())
                                .addLast(authHandler())
                                .addLast(messageHandle());
                    }
                });
        ChannelFuture future  = serverBootstrap.bind(new InetSocketAddress("localhost", beanConfig.getPort())).sync();
        if(future.isSuccess()){
            log.info("Server start success");
        }else{
            log.error("Server start error");
        }
        return serverBootstrap;
    }


}
