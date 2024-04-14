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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
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

    public static void run(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Test test = new Test();

    public static void main(String[] args) throws IOException {
        String port = System.getProperty("port");
        if (port==null){
            port = "8080";
        }
        for (int  i = 0; i< 20;i++){
            executor.execute(Main::run);
        }
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        SocketAddress address = new InetSocketAddress(Integer.valueOf(port));
        serverSocket.bind(address);
        serverSocket.configureBlocking(false);

        final Selector selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("start up");
        while (true){
            if (selector.select(100)<=0){
                continue;
            }
            final Set<SelectionKey> selectionKeys = selector.selectedKeys();
            final Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                final SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()){
                    // 这里为什么不能用 selectionKey.channel()
                    // 要知道，channel方法返回的是 发生事件 的通道，
                    // 而对于accept事件，发生的通道是 serverSocketChannel
                    // 而 clientSocketChannel 是accept事件的结果，selectKey 是不提供结果的，所以无法从selectKey中获取到 client channel
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
        final ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        if (socketChannel.read(byteBuffer)>0){
            byteBuffer.flip();
        }
        String response = dealRequest(byteBuffer.array());
        if (response == null){
            responseB.rewind();
            socketChannel.write(responseB);
        }else{
            socketChannel.write(ByteBuffer.wrap(response.getBytes()));
        }
        socketChannel.close();
    }

    private static String dealRequest(byte[] s) {
        MyHttp request = new MyHttp(new String(s));
        if (Objects.equals(request.uri, "/oom")){
            return test.oomTest();
        }
        if (Objects.equals(request.uri, "/static")){
            return test.testStaticObj();
        }
        return null;
    }





    static class MyHttp {
        private String uri;
        private String method;

        private Map<String,String> header = new HashMap<>();

        private String requestBody;

        private String source;

        private int index = 0;

        public MyHttp(String source) {
            this.source = source;
            method =  getSubString(' ',true);
            uri =  getSubString(' ',true);
            Skip2LineStart();
            initHeader();
            System.out.println();
        }

        private void Skip2LineStart() {
            while (index++ < source.length()){
                if (source.charAt(index) == '\r' && source.charAt(index+1) == '\n'){
                    //结束字符也包含进目标字符串
                    index = index+2;
                    break;
                }
            }
        }

        private String getSubString(char stopFlag, boolean skipFlag){
            int start = index;
            while (index++ < source.length()){
                if (source.charAt(index) == stopFlag){
                    //结束字符也包含进目标字符串
                    index++;
                    break;
                }
            }
            if (skipFlag){
                return source.substring(start,index-1);
            }
            return source.substring(start,index);
        }

        private void initHeader(){
            int start = index;
            String name = null;
            while (index++ < source.length()){
                if (source.charAt(index) == '\r' && source.charAt(index+1) == '\n'){
                    //结束字符也包含进目标字符串
                    header.put(name,source.substring(start,index));
                    name = null;
                    index=index+1;
                    start = index+1;
                    //连续换行，header区域结束
                    if (source.charAt(index+1) == '\r' && source.charAt(index+2) == '\n'){
                        index=index+2;
                        break;
                    }
                }
                if (source.charAt(index) == ':' && source.charAt(index+1) == ' '){
                    name = source.substring(start,index);
                    start = index = index+1;
                }
            }
        }

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
