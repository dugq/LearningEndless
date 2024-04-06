package com.dugq.io.socket;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @author dugq
 * @date 2021/9/9 2:07 下午
 */
public class SocketTest {

    public static void main(String[] args) throws InterruptedException {
        //模拟A给B发一条消息，B对A进行回复
        new Thread(SocketTest::startServerSocket).start();
        TimeUnit.SECONDS.sleep(1);
        new Thread(SocketTest::startClientSocket).start();
    }


    @SneakyThrows
    public static void startServerSocket(){
        ServerSocket serverSocket = new ServerSocket(7777);
        final Socket client = serverSocket.accept();
        final InputStream inputStream = client.getInputStream();
        byte[] bytes = new byte[1024];
        final int length = inputStream.read(bytes);
        System.out.println("client request : "+new String(bytes,0,length));
        client.getOutputStream().write("I do not know what do you say!".getBytes());
        client.getOutputStream().flush();
        client.close();
    }

    @SneakyThrows
    public static void startClientSocket(){
        Socket socket = new Socket("127.0.0.1",7777);
        socket.getOutputStream().write("hello body!".getBytes());
        socket.getOutputStream().flush();
        final InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024];
        final int length = inputStream.read(bytes);
        System.out.println("server response : "+new String(bytes,0,length));
        socket.close();
    }
}
