package com.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dugq on 2023/5/12.
 */
public class Main {
    private static ExecutorService executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), new ThreadFactory() {
        private AtomicInteger index = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("dgq-NIO-"+index.getAndAdd(1));
            return thread;
        }
    });

    private static String response = "HTTP/1.1 200 OK\n" +
            "Server: CLOUD ELB 1.0.0\n" +
            "Date: Sat, 06 May 2023 02:09:11 GMT\n" +
            "Content-Type: text/plain; charset=utf-8\n" +
            "host: 127.0.0.1\n" +
            "Connection: keep-alive\n\n"+
            "hello";
    private static ByteBuffer responseB;

    static {
        int length = response.getBytes().length;
        responseB = ByteBuffer.allocateDirect(length);
        responseB.put(response.getBytes());
    }
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        SocketAddress address = new InetSocketAddress("127.0.0.1",8089);
        serverSocket.bind(address);
        serverSocket.configureBlocking(false);

        final Selector selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            if (selector.selectNow()==0){
                continue;
            }
            final Set<SelectionKey> selectionKeys = selector.selectedKeys();
            final Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                final SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()){
                    SelectableChannel clientChannel = serverSocket.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector,SelectionKey.OP_READ);
                }
                if (selectionKey.isReadable()){
                    final SocketChannel clientChannel = (SocketChannel)selectionKey.channel();
                    selectionKey.cancel();
                    executor.execute(new CallBackRunnable(clientChannel));
                }
                iterator.remove();
            }
        }
    }

    static void doOperation(SocketChannel socketChannel) throws IOException {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (socketChannel.read(byteBuffer)>0){
            byteBuffer.clear();
        }
        ByteBuffer wrap = ByteBuffer.wrap(response.getBytes());
        socketChannel.write(wrap);
        socketChannel.close();
    }

    static class CallBackRunnable implements Runnable{
        private SocketChannel socket;
        public CallBackRunnable(SocketChannel socket){
            this.socket = socket;
        }
        public void run(){
            try {
                doOperation(socket);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (socket.isConnected()){
                        socket.close();
                    }
                } catch (IOException ex) {
                    e.printStackTrace();
                }
            }
        }

    }
}
