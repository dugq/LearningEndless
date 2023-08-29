package com.example.learn.io.NIO;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;import java.net.InetSocketAddress;import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


/**
 * Created by dugq on 2023/8/8.
 */
public class NioClient {

    public static void main(String[] args) throws IOException {
        clientSendMsg();
    }

    @Test
    public void test() throws IOException {
        NioClient.clientSendMsg();
    }

    private static void clientSendMsg() throws IOException {
        Selector selector = Selector.open();
        final SocketChannel clientChannel = SocketChannel.open();
//        clientChannel.configureBlocking(false);
//        clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        boolean connected = clientChannel.connect(new InetSocketAddress("localhost", 9999));
        System.out.println(connected);
        clientChannel.write(ByteBuffer.wrap("hello!".getBytes()));
        clientChannel.close();
        while (true){
            int select = selector.select(1000);
            if (select<=0){
                continue;
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isReadable()){
                    if (readRcvAndSendMsg(selectionKey)){
                        return;
                    }
                }
                if (selectionKey.isWritable()){
                    System.out.println("connected");
                    selectionKey.interestOps(selectionKey.interestOps() ^ SelectionKey.OP_WRITE);
                    clientChannel.write(ByteBuffer.wrap("hello boy!".getBytes()));
                }
                iterator.remove();
            }
        }

    }

    private static boolean readRcvAndSendMsg(SelectionKey selectionKey) throws IOException {
        System.out.println("read start ");
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        String message = doRead(channel);
        System.out.println("rcv : "+message);
        if (StringUtils.equals("closed",message)){
            return true;
        }
        return sendMsg(channel);
    }

    private static boolean sendMsg(SocketChannel channel) throws IOException {
        byte[] bytes = new byte[1024];
        int len = System.in.read(bytes)-1;
        if (isClose(bytes,len)){
            channel.close();
            return true;
        }
        ByteBuffer sendMsg = ByteBuffer.wrap(bytes,0,len);
        channel.write(sendMsg);
        return false;
    }



    static byte[] close = "close".getBytes();
    private static boolean isClose(byte[] bytes, int len) {
        if (len != close.length){
            return false;
        }
        for (int i =0 ;i < close.length; i++){
            if (close[i] != bytes[i]){
                return false;
            }
        }
        return true;
    }

    private static String doRead(SocketChannel channel) throws IOException {
        final ByteBuffer response = ByteBuffer.allocate(1024);
        int length = channel.read(response);
        if (length<=0){
            if (length<0){
                channel.close();
                return "closed";
            }
            return "";
        }
        return new String(response.array(),0,length);
    }
}

