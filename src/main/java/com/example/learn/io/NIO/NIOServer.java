package com.example.learn.io.NIO;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
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
        clientSendMsg();
    }

    private static void clientSendMsg() throws IOException {
        final SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9999));
        clientChannel.write(ByteBuffer.wrap("hello boy!".getBytes()));
        final ByteBuffer response = ByteBuffer.allocate(1024);
        final int length = clientChannel.read(response);
        System.out.println("server response: "+new String(response.array(),0,length));
    }

    @SneakyThrows
    private static void startServerClient() {
        ExecutorService executor = Executors.newFixedThreadPool(5);//线程池

        final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9999));
        serverSocketChannel.configureBlocking(false);

        final Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true){
            if (selector.selectNow()==0){
                continue;
            }
            final Set<SelectionKey> selectionKeys = selector.selectedKeys();
            final Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                final SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()){
                    SelectableChannel clientChannel = serverSocketChannel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector,SelectionKey.OP_READ);
//                    System.out.println("accept one client:"+clientChannel.hashCode());
                }
                if (selectionKey.isReadable()){
                    final SocketChannel clientChannel = (SocketChannel)selectionKey.channel();
                    System.out.println("client can read:"+clientChannel.hashCode());
                    selectionKey.cancel();
                    // doOperation(clientChannel);
                    executor.execute(()->doOperation(clientChannel));
                }
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
    private static void doOperation(SocketChannel socketChannel){
        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        final int length = socketChannel.read(byteBuffer);
        System.out.println("client request : "+new String(byteBuffer.array(),0,length));
        socketChannel.write(ByteBuffer.wrap(response.getBytes()));
        socketChannel.close();
    }
}
