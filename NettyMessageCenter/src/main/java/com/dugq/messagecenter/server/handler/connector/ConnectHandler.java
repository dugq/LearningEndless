package com.dugq.messagecenter.server.handler.connector;

import com.dugq.agreement.AgreementDecoder;
import com.dugq.agreement.AgreementEncoder;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by dugq on 2024/4/15.
 */
@Slf4j
public class ConnectHandler extends ChannelInboundHandlerAdapter {
    private final ConnectionManger connectionManger;
    private boolean isLongConnect;

    private String myAgreementPath;

    public ConnectHandler(ConnectionManger connectionManger,String myAgreementPath) {
        this.myAgreementPath = myAgreementPath;
        this.connectionManger = connectionManger;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ChannelPipeline p = ctx.pipeline();
        if(msg instanceof HttpRequest){
            final FullHttpRequest req = (FullHttpRequest) msg;
            if(isNotWebSocketPath(req) || isLongConnect){
                super.channelRead(ctx, msg);
                return;
            }
            try {
                shark(ctx, p, req);
            }catch (Exception e){
                log.error("Failed to upgrade to message center agreement", e);
                ctx.channel().close();
            }
        }else{
            // 鉴别只发生一次。无论如何只会鉴别一次哦。
            p.remove(this);
            super.channelRead(ctx,msg);
        }
    }

    private void shark(ChannelHandlerContext ctx, ChannelPipeline p, FullHttpRequest req) {
        // 添加自定义协议编解码
        p.addFirst("agreement-decoder",new AgreementDecoder());
        p.addFirst("agreement-encode",new AgreementEncoder());
        connectionManger.add(ctx);
        isLongConnect = true;
        //回复连接成功，已经启动自定义服务
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS,
                req.content().alloc().buffer(0));
        ctx.channel().writeAndFlush(res).addListener((ChannelFutureListener) future -> {
            if(future.isSuccess()){
                // 移除http服务
                if (p.get(HttpObjectAggregator.class) != null) {
                    p.remove(HttpObjectAggregator.class);
                }
                if (p.get(HttpContentCompressor.class) != null) {
                    p.remove(HttpContentCompressor.class);
                }
                if (p.get(HttpResponseEncoder.class) != null) {
                    p.remove(HttpResponseEncoder.class);
                }
                if (p.get(HttpRequestDecoder.class) != null) {
                    p.remove(HttpRequestDecoder.class);
                }
                if (p.get(HttpServerCodec.class) != null) {
                    p.remove(HttpServerCodec.class);
                }
            }else{
                ctx.channel().close();
            }
        });
    }

    private boolean isNotWebSocketPath(FullHttpRequest req) {
        return !req.uri().startsWith(myAgreementPath);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        if (isLongConnect){
            connectionManger.remove(ctx);
        }
    }
}
