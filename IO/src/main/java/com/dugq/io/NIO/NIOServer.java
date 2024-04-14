package com.dugq.io.NIO;

import com.dugq.ThreadUtil;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dugq
 * @date 2021/10/11 12:39 上午
 */
public class NIOServer {

    @SneakyThrows
    public static void main(String[] args) {
        new Thread(NIOServer::startServerClient).start();
    }

    static ExecutorService executor = Executors.newFixedThreadPool(5);//线程池
    @SneakyThrows
    private static void startServerClient() {


        final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9999));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.accept();

        final Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            if (selector.select(1000)==0){
                continue;
            }
            final Set<SelectionKey> selectionKeys = selector.selectedKeys();
            final Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                final SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()){
                    SocketChannel clientChannel =  serverSocketChannel.accept();
                    clientChannel.configureBlocking(false);
                    SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
                    clientKey.attach(new MyContent(clientChannel,clientKey));
                    System.out.println("client connected!");
                }
                if (selectionKey.isReadable()){
                    ThreadUtil.sleep(3);
                    doOperation((MyContent)selectionKey.attachment());
                }
                //
                iterator.remove();
            }
        }
    }
    private static String response = "HTTP/1.1 200 OK\n" +
            "Server: CLOUD ELB 1.0.0\n" +
            "Date: Sat, 06 May 2023 02:09:11 GMT\n" +
            "Content-Type: text/plain; charset=utf-8\n" +
            "host: 127.0.0.1\n" +
            "Connection: keep-alive\n\n"+
            "hello";

    @SneakyThrows
    private static void doOperation( MyContent content){
        System.out.println("read start ");
        SocketChannel socketChannel = content.socketChannel;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int length;
        try {
            // 相比reactor 主从模型耗时点
            // read 需要从内核空间复制到直接内存，再复制到堆内存
             length = socketChannel.read(byteBuffer);
        }catch (Exception e){
            e.printStackTrace();
            // 发送异常时，如若没有关闭连接，TCP连接会持续卡在内核内存中
//            socketChannel.close();
            return;
        }
        if (length<=0){
            // 必须正确的关闭连接，否则tcp连接将固定在close_wait 2小时，期间持续占用内存
            // 而且必须立刻响应。如果不是立刻响应，期间tcp 可能会不定期发送窗口变更通知/轮训状态验证等消息，
            // 由于对端已经关闭处于time_wait阶段，它会回复RST_ack消息，服务端下次发送消息时就会发生 connect peer rest 异常
                content.clientKey.cancel();
            System.out.println("client closed itself!");
            socketChannel.close();
            return;
        }
        byteBuffer.flip();
        System.out.println("client request : "+new String(byteBuffer.array(),0,length));
        sendMsg(socketChannel);
//        executor.execute(()->{
//            try {
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                if (socketChannel.isConnected() || socketChannel.isOpen()){
//                    try {
//                        socketChannel.close();
//                    } catch (IOException ex) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
    }

    private static void sendMsg(SocketChannel channel) throws IOException {
        byte[] bytes = new byte[1024];
        int len = System.in.read(bytes)-1;
        if (!channel.isConnected() || !channel.isOpen()){
            System.out.println("channel is already closed!");
            return;
        }
        System.out.println("read size = "+len);
        if (isClose(bytes,len)){
            channel.close();
            return;
        }
        if (len<=0){
            System.out.println("blank msg send!");
        }
        ByteBuffer sendMsg = ByteBuffer.wrap(bytes,0,len);
        channel.write(sendMsg);
        System.out.println("send over");
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

    static class MyContent{
         SocketChannel socketChannel;
        SelectionKey clientKey;

        public MyContent(SocketChannel socketChannel, SelectionKey clientKey) {
            this.socketChannel = socketChannel;
            this.clientKey = clientKey;
        }

    }



}
