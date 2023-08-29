package com.example.learn.io.BIO;

import lombok.SneakyThrows;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import javax.servlet.http.HttpServletResponse;
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
public class BIOServerThreadModel {

    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(100);//线程池
        ServerSocket serverSocket = new ServerSocket(8088);
        System.out.println("start up ");
        while (!Thread.currentThread().isInterrupted()) {//主线程死循环等待新连接到来
            Socket socket = serverSocket.accept();
//            doOperation(socket);



            /**
             * 1、当前线程继续处理。
             * doOperation(socket);
             */
//            new Thread(()->doOperation(socket)).start();
            /**
             * 2、新起线程处理
             * new Thread(()->doOperation(socket)).start();
             *
             */

            /**
             * 3、使用线程池进行处理
             */
            executor.execute(new CallBackRunnable(socket));//为新的连接创建新的线程
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
                int len = inputStream.read(buffer);
                final OutputStream outputStream = socket.getOutputStream();
//            ctrl 业务处理
                outputStream.write(("you say "+ len+" words").getBytes());
                outputStream.flush();
            }
        }

    }


}
