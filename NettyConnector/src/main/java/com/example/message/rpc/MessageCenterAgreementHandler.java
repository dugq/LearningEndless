package com.example.message.rpc;

import com.dugq.agreement.AgreementDecoder;
import com.dugq.agreement.AgreementEncoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

/**
 * Created by dugq on 2024/4/17.
 */
public class MessageCenterAgreementHandler extends ChannelInboundHandlerAdapter {
    private MessageSendHandle messageSendHandle;

    public MessageCenterAgreementHandler(MessageSendHandle messageSendHandle) {
        this.messageSendHandle = messageSendHandle;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpResponse && ((FullHttpResponse)msg).status().code()==101){
              ctx.pipeline().addLast(new AgreementDecoder())
                    .addLast(new AgreementEncoder())
                    .addLast(messageSendHandle);
            ChannelPipeline pipeline = ctx.pipeline();
            if (pipeline.get(HttpObjectAggregator.class)!=null){
                ctx.pipeline().remove(HttpObjectAggregator.class);
            }
            if (pipeline.get(HttpClientCodec.class)!=null){
                ctx.pipeline().remove(HttpClientCodec.class);
            }
            if (pipeline.get(HttpResponseDecoder.class)!=null){
                ctx.pipeline().remove(HttpResponseDecoder.class);
            }
            if (pipeline.get(HttpRequestEncoder.class)!=null){
                ctx.pipeline().remove(HttpRequestEncoder.class);
            }
            ctx.pipeline().remove(this);

        }
    }
}
