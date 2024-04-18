package com.example.connection;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by dugq on 2024/4/15.
 */
@Slf4j
public class MyWebSocketFrameEncoder {

    private static final byte OPCODE_CONT = 0x0;
    private static final byte OPCODE_TEXT = 0x1;
    private static final byte OPCODE_BINARY = 0x2;
    private static final byte OPCODE_CLOSE = 0x8;
    private static final byte OPCODE_PING = 0x9;
    private static final byte OPCODE_PONG = 0xA;

    // 构建WebSocketFrame
    public static ByteBuf encode(WebSocketFrame msg){
        final ByteBuf data = msg.content();
        byte[] mask;

        byte opcode;
        if (msg instanceof TextWebSocketFrame) {
            opcode = OPCODE_TEXT;
        } else if (msg instanceof PingWebSocketFrame) {
            opcode = OPCODE_PING;
        } else if (msg instanceof PongWebSocketFrame) {
            opcode = OPCODE_PONG;
        } else if (msg instanceof CloseWebSocketFrame) {
            opcode = OPCODE_CLOSE;
        } else if (msg instanceof BinaryWebSocketFrame) {
            opcode = OPCODE_BINARY;
        } else if (msg instanceof ContinuationWebSocketFrame) {
            opcode = OPCODE_CONT;
        } else {
            throw new UnsupportedOperationException("Cannot encode frame of type: " + msg.getClass().getName());
        }

        int length = data.readableBytes();

        if (log.isTraceEnabled()) {
            log.trace("Encoding WebSocket Frame opCode={} length={}", opcode, length);
        }

        // 大于65535个字节，强制报警
        if(length>0xFFFF){
            throw new UnsupportedOperationException("the message is too large! length="+length);
        }

        // 构建一个帧的数据：使用writeBytes写是数据，每次写入一个byte类型数字。
        // 第一个8位包含：
        // FIN 是否终止帧 1bit，
        // SRV1-3 3bit，预留字段，无意义，填0即可
        // 帧类型opcode，4位，包含text，ping，pong，close，binary，cont
        int b0 = 0;
        // 如果是FIN帧，最高位设为1
        if (msg.isFinalFragment()) {
            b0 |= 1 << 7;
        }
        // 或运算，保真性。把低4位的补充上
        b0 |= opcode;

        // 理论来说，ping 和pong都是不能带数据的
        if (opcode == OPCODE_PING && length > 125) {
            throw new TooLongFrameException("invalid payload for PING (payload length must be <= 125, was "
                    + length);
        }

        boolean release = true;
        ByteBuf buf = null;
        try {
            // 消息体小于 127 -2 字节时，可以构建简短消息，
            // 帧头只有两个字节 一个字节帧信息，一个字节消息体长度，后面追加帧消息题即可
            if (length <= 125) {
                int size = length + 2;
                buf = PooledByteBufAllocator.DEFAULT.directBuffer(size);
                // 帧类型
                buf.writeByte(b0);
                // 消息体长度
                buf.writeByte(length);
                // 消息体
                buf.writeBytes(data);
                release = false;
                return buf;
            } else {
                //构建4字节消息头
                buf =  PooledByteBufAllocator.DEFAULT.directBuffer(4);
                // 1字节，消息类型
                buf.writeByte(b0);
                // 1字节消息长度，由于长度超过了127个字节，所以这里设置啥都不对，
                // 因此把由于，帧头占用了2位，所以实际消息体最大只能125位，这里设定了两个特殊值：126/127，表示此位作废
                // 126 表示使用后面2个字节存储实际消息体长度
                // 127 表示使用后面6个字节存储实际消息体长度
                // 这里我们强制了消息最多不能超过65535，所以两个字节足够了。
                buf.writeByte(126);
                //先写高4位
                buf.writeByte(length >>> 8 & 0xFF);
                //再写低4位
                buf.writeByte(length & 0xFF);
                // 使用组合消息，避免一次复制
                CompositeByteBuf composite = PooledByteBufAllocator.DEFAULT.compositeBuffer();
                data.retain();
                composite.addComponents(true,buf,data);
                release = false;
                return composite;

            }
        } finally {
            // 构建失败，则把已构建的释放掉
            if (release && buf != null) {
                buf.release();
            }
        }
    }
}
