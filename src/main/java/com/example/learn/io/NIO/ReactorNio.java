package com.example.learn.io.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by dugq on 2023/6/25.
 */
public class ReactorNio {

    public static void main(String[] args) throws IOException {

        final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9999));
        serverSocketChannel.configureBlocking(false);
        final Selector selector = Selector.open();
        new AcceptRunner(serverSocketChannel,selector);
    }

    static class AcceptRunner implements Runnable{
        final private ServerSocketChannel serverSocketChannel;
        final private Selector selector;

        public AcceptRunner(ServerSocketChannel serverSocketChannel, Selector selector) throws ClosedChannelException {
            this.serverSocketChannel = serverSocketChannel;
            this.selector =selector;
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }

        @Override
        public void run() {
            try {
                SelectableChannel accept = serverSocketChannel.accept();
                new ReadHandler((SocketChannel)accept,selector);
            } catch (IOException e) {

            }
        }
    }

    static class ReadHandler implements Runnable{

        private final SelectionKey selectionKey;

        public ReadHandler(SocketChannel socketChannel, Selector selector) throws IOException {
            socketChannel.configureBlocking(false);
            selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
            selectionKey.attach(this);
        }

        @Override
        public void run() {
            SocketChannel clientChannel = (SocketChannel)selectionKey.channel();
            ByteBuffer allocate = ByteBuffer.allocate(1024);
            try {
               while (clientChannel.read(allocate)>0){

               }
                //do something
               clientChannel.write(ByteBuffer.wrap("你好啊".getBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
