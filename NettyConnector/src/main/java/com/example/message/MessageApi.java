package com.example.message;

import com.dugq.agreement.LocationAgreement;
import com.dugq.agreement.MessageAgreement;
import com.dugq.agreement.MessageCenterAgreement;
import com.dugq.agreement.ReceiveAgreement;
import com.dugq.message.ByteMessage;
import com.example.message.rpc.NettyClient;
import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Created by dugq on 2024/4/16.
 */
@Component
@Slf4j
public class MessageApi extends SingleThreadEventExecutor {

    private NettyClient nettyClient;

    protected MessageApi() {
        super(null, r -> {
            Thread thread = new Thread(r);
            thread.setName("message-api-thread");
            return thread;
        }, true);
    }

    public void setNettyClient(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    private void sendMessage(MessageCenterAgreement messageCenterAgreement){
        nettyClient.sendMessage(messageCenterAgreement, future -> {
            if(!future.isSuccess()){
                log.error("error : ",future.cause());
                System.out.println(future.cause().getMessage());
//                addMessage(messageCenterAgreement);
            }
        });
    }

    public void locationMessage(int messageId){
        LocationAgreement locationAgreement = new LocationAgreement(Collections.singletonList(messageId),"netty-connector");
        addMessage(locationAgreement);
    }

    public void byteMessage(ByteBuf byteBuf){
        ByteMessage byteMessage = new ByteMessage();
        byteMessage.setType((byte) 1);
        byteMessage.setContent(byteBuf);
        MessageAgreement agreement = new MessageAgreement(byteMessage);
        addMessage(agreement);
    }

    public void sendSuccess(int messageId,int count){
        ReceiveAgreement receiveAgreement= new ReceiveAgreement(messageId,count);
        addMessage(receiveAgreement);
    }

    private void addMessage(MessageCenterAgreement message){
        execute(()-> sendMessage(message));
    }

    @Override
    protected void run() {
        for (;;) {
            Runnable task = takeTask();
            if (task != null) {
                task.run();
                updateLastExecutionTime();
            }

            if (confirmShutdown()) {
                break;
            }
        }
    }
}
