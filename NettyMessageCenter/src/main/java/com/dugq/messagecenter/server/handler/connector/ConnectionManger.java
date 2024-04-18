package com.dugq.messagecenter.server.handler.connector;

import com.dugq.agreement.MessageAgreement;
import com.dugq.message.MessageContent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dugq on 2024/4/15.
 */
public class ConnectionManger {

    private ConcurrentHashMap<String, NioSocketChannel> clients = new ConcurrentHashMap<>();
    public void add(ChannelHandlerContext ctx) {
        String ip = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
        clients.put(ip, (NioSocketChannel)ctx.channel());
        System.out.println("add ");
    }

    public void remove(ChannelHandlerContext ctx) {
        String ip = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
        clients.remove(ip);
    }

    public void send(MessageContent messageContent){

    }

    public void batchSend(List<MessageContent> messages){
        MessageAgreement messageAgreement = new MessageAgreement(messages);
        for (NioSocketChannel client : clients.values()) {
            client.writeAndFlush(messageAgreement);
        }
    }

}
