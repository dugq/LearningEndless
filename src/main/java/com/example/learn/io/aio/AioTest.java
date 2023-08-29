package com.example.learn.io.aio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;

/**
 * Created by dugq on 2023/6/8.
 */
public class AioTest {

    private static String response = "HTTP/1.1 200 OK\n" +
            "Server: CLOUD ELB 1.0.0\n" +
            "Date: Sat, 06 May 2023 02:09:11 GMT\n" +
            "Content-Type: text/plain; charset=utf-8\n" +
            "host: 127.0.0.1\n" +
            "Connection: keep-alive\n\n"+
            "hello";

    @Test
    public void aio() throws IOException {
        AsynchronousServerSocketChannel channel =  AsynchronousServerSocketChannel.open();
        channel.bind(new InetSocketAddress("localhost",8098));
        channel.accept(channel,new MyAcceptHandler());
        int read = System.in.read();
    }

    static class MyAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>{
        @Override
        public void completed(AsynchronousSocketChannel client, AsynchronousServerSocketChannel attachment) {
            attachment.accept(attachment,this);
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            client.read(byteBuffer,byteBuffer,new ReaderHandler(client));
        }

        @Override
        public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
            exc.printStackTrace();
        }
    }

    static class ReaderHandler implements CompletionHandler<Integer,ByteBuffer>{
        private final AsynchronousSocketChannel client;
        public ReaderHandler(AsynchronousSocketChannel client) {
            this.client = client;
        }

        @Override
        public void completed(Integer length, ByteBuffer attachment) {
            System.out.println(new String(attachment.array()));
            ByteBuffer byteBuffer = ByteBuffer.wrap(response.getBytes());
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()){
                client.write(byteBuffer);
            }
            try {
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            exc.printStackTrace();
        }
    }
}
