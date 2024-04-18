package com.example.connection;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dugq on 2024/4/12.
 */
public class MyAgreement {

    // 1 byte 0-127
    private MyInstruct instruct;

    // 消息体，长度不一，各自解析
    private ByteBuf message;


    private static Map<Character, MyInstruct> map = new HashMap<>();

    static {
        for (MyInstruct value : MyInstruct.values()) {
            map.put(value.value, value);
        }
    }


    public MyAgreement() {

    }

    public static MyAgreement build(ByteBuf message){
        MyInstruct instruct = getMyInstruct(message.readByte());
        if (instruct == null){
            return null;
        }
        MyAgreement myAgreement = new MyAgreement();
        myAgreement.instruct = instruct;
        myAgreement.message = message;
        return myAgreement;
    }

    public MyInstruct getInstruct() {
        return instruct;
    }

    public ByteBuf getMessage() {
        return message;
    }

    public ByteBuf getMessageOnly(){
        int len = message.readableBytes();
        message.markReaderIndex();
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(len);
        message.readBytes(byteBuf);
        message.resetReaderIndex();
        return byteBuf;
    }

    public static MyInstruct getMyInstruct(byte value) {
        return map.get((char)value);
    }

    public enum MyInstruct {
        ERROR('0'),
        LOGIN('1'),
        MESSAGE('2'),

        REPLY_LOGIN('3'),

        REPLY_MESSAGE('4')
        ;

        private final char value;

        MyInstruct(char value) {
            this.value =value;
        }

    }

    public enum ReplyCode {
        LOGIN_SUCCESS("登陆成功");



        private final String code;

        ReplyCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }


    public enum ErrorCode {
        AUTH_ERROR("登陆错误"),
        AGREEMENT_ERROR("协议错误"),
        NOT_LOGIN("未登录")
        ;
        private final String code;



        ErrorCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }


    public static TextWebSocketFrame buildMyAgreementFrame(MyInstruct instruct,String message){
        byte[] bytes = message.getBytes();
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(2+bytes.length);
        byteBuf.writeByte(instruct.value);
        byteBuf.writeBytes(bytes);
        TextWebSocketFrame frame = new TextWebSocketFrame(byteBuf);
        return frame;
    }


    public static TextWebSocketFrame buildMyAgreementFrame(MyInstruct instruct){
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(2);
        byteBuf.writeByte(instruct.value);
        TextWebSocketFrame frame = new TextWebSocketFrame(byteBuf);
        return frame;
    }

    public static ByteBuf buildMyAgreementFrame(MyInstruct instruct,ByteBuf message){
        int length = message.readableBytes()+2;
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(length);
        byteBuf.writeByte(instruct.value);
        byteBuf.writeBytes(message);
        return byteBuf;
    }
}
