package com.example.io.BIO;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dugq
 * @date 2021/7/19 6:02 下午
 */
public class BIOServer {

    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(100);//线程池
        ServerSocket serverSocket = new ServerSocket(8088);
        while (!Thread.currentThread().isInterrupted()) {//主线程死循环等待新连接到来
            Socket socket = serverSocket.accept();
            System.out.println("accept");
            executor.execute(new CallBackRunnable(socket));//为新的连接创建新的线程
        }
    }

       static class BaseRunnable implements Runnable{
            private Socket socket;
            public BaseRunnable(Socket socket){
                this.socket = socket;
            }
            @SneakyThrows
            public void run(){
                while(!Thread.currentThread().isInterrupted()&&!socket.isClosed()){//死循环处理读写事件
                    final InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0 ;
                    while ((len = inputStream.read(buffer))>0){
                        System.out.println("length = "+len+"\tcontent=" +new String(buffer,0,len));
                        buffer = new byte[1024];
                    }
                    System.out.println("read over");
                }
                System.out.println("closed");
            }
        }

    static class CallBackRunnable implements Runnable{
        private Socket socket;
        public CallBackRunnable(Socket socket){
            this.socket = socket;
        }
        @SneakyThrows
        public void run(){
            while(!Thread.currentThread().isInterrupted()&&!socket.isClosed()){//死循环处理读写事件
                final InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0 ;
                StringBuilder sb = new StringBuilder();
                while ((len = inputStream.read(buffer))>0){
                    final String content = new String(buffer, 0, len);
                    System.out.println("length = "+len+"\tcontent=" + content);
                    if (len==2){
                        if (buffer[0]==-1 && buffer[1]==-20){
                            break;
                        }
                    }
                    sb.append(content);
                    buffer = new byte[1024];
                }
                System.out.println("read over");
                final OutputStream outputStream = socket.getOutputStream();
                outputStream.write("what are you say?".getBytes());
                outputStream.flush();
            }
            System.out.println("closed");
        }
    }

}
