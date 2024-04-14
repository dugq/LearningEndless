package com.example.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Created by dugq on 2024/4/12.
 */
@ChannelHandler.Sharable
@Slf4j
public class AuthHandler extends ChannelInboundHandlerAdapter {
    @Resource
    private ConnectionMangers connectionMangers;

    private final ThreadLocal<TextWebSocketFrame> errorByteBuf = new ThreadLocal<>();

    private final ThreadLocal<TextWebSocketFrame> notLoginError = new ThreadLocal<>();

    private final ThreadLocal<TextWebSocketFrame> loginSuccess = new ThreadLocal<>();


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MyAgreement) {
            MyAgreement agreement = (MyAgreement) msg;
            if(agreement.getInstruct()== MyAgreement.MyInstruct.LOGIN){
                handleLogin(agreement,ctx);
            }else if(isLogin(ctx)){
                ctx.fireChannelRead(msg);
            }else{
                ctx.writeAndFlush(getNotLoginError());
            }
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private boolean isLogin(ChannelHandlerContext ctx) {
        return Objects.nonNull(ctx.channel().attr(userIdKey)) && Objects.nonNull(ctx.channel().attr(userIdKey).get());
    }

    private void handleLogin(MyAgreement agreement,ChannelHandlerContext ctx) {
        if(isLogin(ctx)){
            ctx.writeAndFlush(getLoginSuccess());
            return;
        }
        Long id = getId(agreement.getMessage());
        if(id == null){
            ctx.writeAndFlush(getErrorByteBuf());
        }else{
            ctx.channel().attr(userIdKey).set(id);
            connectionMangers.addChannel(id, ctx);
            ctx.writeAndFlush(getLoginSuccess());
        }
    }

    private TextWebSocketFrame getErrorByteBuf(){
        TextWebSocketFrame message = errorByteBuf.get();
        if (message != null){
            message.retain();
            message.content().resetReaderIndex();
            return message;
        }
        message = MyAgreement.buildMyAgreementFrame(MyAgreement.MyInstruct.ERROR,MyAgreement.ErrorCode.AUTH_ERROR.getCode());
        errorByteBuf.set(message);
        message.retain();
        return message;
    }

    private TextWebSocketFrame getNotLoginError(){
        TextWebSocketFrame message = notLoginError.get();
        if (message != null){
            message.retain();
            message.content().resetReaderIndex();
            return message;
        }
        message = MyAgreement.buildMyAgreementFrame(MyAgreement.MyInstruct.ERROR,MyAgreement.ErrorCode.NOT_LOGIN.getCode());
        notLoginError.set(message);
        message.retain();
        return message;
    }

    private TextWebSocketFrame getLoginSuccess(){
        TextWebSocketFrame message = loginSuccess.get();
        if (message != null){
            message.retain();
            message.content().resetReaderIndex();
            return message;
        }
        message = MyAgreement.buildMyAgreementFrame(MyAgreement.MyInstruct.REPLY,MyAgreement.ReplyCode.LOGIN_SUCCESS.getCode());
        notLoginError.set(message);
        message.retain();
        return message;
    }

    private Long getId(ByteBuf message){
        int size = message.readableBytes();
        if (size <=0 || size>10) {
            return null;
        }
        long id = 0;
        while(message.readableBytes()>0){
            byte b = message.readByte();
            if(b<'0' || b>'9'){
                return null;
            }
            id =id*10+ b -'0';
        }
        return id;
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive");
        logout(ctx.channel());
        super.channelInactive(ctx);
    }

    private void logout(Channel channel){
        Attribute<Long> attr = channel.attr(userIdKey);
        if (attr != null && attr.get()!= null){
            connectionMangers.removeChannel(attr.get(),channel);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelUnregistered");
        logout(ctx.channel());
        super.channelInactive(ctx);
    }

    private static final AttributeKey<Long> userIdKey = AttributeKey.newInstance("userId");
}
