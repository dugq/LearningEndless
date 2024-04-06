package com.dugq.io.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dugq on 2023/6/9.
 */
public class NettyServer {
    private static String response = "hello !!!! ";
    private static ByteBuf res = ByteBufAllocator.DEFAULT.directBuffer().writeBytes(response.getBytes());

    @Test
    public void serverStarter() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(new ThreadFactory(){
            AtomicInteger index = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("netty-boos-"+index.getAndAdd(1));
                return thread;
            }
        });
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        new Bootstrap();
        ServerBootstrap aggregator = serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 2048)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast("http-codec", new HttpServerCodec())
                                .addLast("aggregator", new HttpObjectAggregator(65535))
                                .addLast("http-chunked", new ChunkedWriteHandler())
                                .addLast(new MyHandler());
                    }
                });
        ChannelFuture channelInitFuture = aggregator.bind(new InetSocketAddress("localhost", 8088));
        ChannelFuture localhost = channelInitFuture.sync();
        if (localhost.isSuccess()){
            System.out.println("server start up !");
        }
        Channel channel = localhost.channel();
        ChannelFuture channelClosedFuture = channel.closeFuture();
        channelClosedFuture.sync();
    }

    static class MyHandler extends ChannelInboundHandlerAdapter {
        private static WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory("ws://localhost:8088/websocket",null,false);
        private WebSocketServerHandshaker ss ;
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof FullHttpRequest){
                dealHttpRequest(ctx,(FullHttpRequest)msg);
            }
            if (msg instanceof WebSocketFrame){
                handlerWSMsg(ctx,(WebSocketFrame)msg);
            }
            ctx.channel().flush();
        }

        private void handlerWSMsg(ChannelHandlerContext ctx, WebSocketFrame msg) {
            if (msg instanceof TextWebSocketFrame){
                handlerMsg(ctx,(TextWebSocketFrame)msg);
            }
            else if (msg instanceof PingWebSocketFrame){
                ctx.channel().write(new PongWebSocketFrame());
            }else if (msg instanceof CloseWebSocketFrame){
                ss.close(ctx.channel(),(CloseWebSocketFrame)msg.retain());
            }
        }

        private void handlerMsg(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
            ctx.channel().write(new TextWebSocketFrame("服务端已经收到您的消息："+msg.text()));
        }

        private void dealHttpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
            if (StringUtils.equals(msg.uri(),"/websocket")){
                 ss = webSocketServerHandshakerFactory.newHandshaker(msg);
                if (ss==null){
                    WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                    ctx.close();
                }
                ss.handshake(ctx.channel(),msg);
            }else{
                res.readerIndex(0);
                res.retain();
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,res);
                ctx.channel().writeAndFlush(response);
                ctx.channel().close();
                System.out.println("ref count : "+res.refCnt());
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            super.channelReadComplete(ctx);
        }


    }

}
