package com.example;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dugq on 2023/5/6.
 */
public class Main {
    private static ExecutorService executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), new ThreadFactory() {
        private AtomicInteger index = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("dgq-Bio-"+index.getAndAdd(1));
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
    public static void main(String[] args) throws IOException {
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

    static void doOperation(Socket socket) throws IOException {
        while(!Thread.currentThread().isInterrupted()&&!socket.isClosed()){//死循环处理读写事件
            final InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            if (len>0){
                final OutputStream outputStream = socket.getOutputStream();
//            ctrl 业务处理
                outputStream.write(response.getBytes());
//                outputStream.write(("you say "+ len+" words\n").getBytes());
                outputStream.flush();
                socket.close();
            }
        }
    }

    static class CallBackRunnable implements Runnable{
        private Socket socket;
        public CallBackRunnable(Socket socket){
            this.socket = socket;
        }
        public void run(){
            try {
                doOperation(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
