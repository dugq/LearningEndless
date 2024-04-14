package com.example.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Created by dugq on 2024/4/12.
 */
@ChannelHandler.Sharable
public class MyAgreementHandler extends ChannelInboundHandlerAdapter {
    private ThreadLocal<TextWebSocketFrame> errorAgreementByteBuf = new ThreadLocal<>();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof TextWebSocketFrame){
            TextWebSocketFrame frame = (TextWebSocketFrame) msg;
            ByteBuf content = frame.content();
            MyAgreement agreement = MyAgreement.build(content);
            if (agreement==null){
                content.release();
                ctx.writeAndFlush(getErrorByteBuf());
                agreement.getMessage().release();
                return;
            }
            ctx.fireChannelRead(agreement);
        }else{
            ctx.fireChannelRead(msg);
        }

    }

    private TextWebSocketFrame getErrorByteBuf(){
        TextWebSocketFrame message = errorAgreementByteBuf.get();
        if (message != null){
            message.retain();
            message.content().resetReaderIndex();
            return message;
        }
        message = MyAgreement.buildMyAgreementFrame(MyAgreement.MyInstruct.MESSAGE,MyAgreement.ErrorCode.AGREEMENT_ERROR.getCode());
        errorAgreementByteBuf.set(message);
        message.retain();
        return message;
    }

}
