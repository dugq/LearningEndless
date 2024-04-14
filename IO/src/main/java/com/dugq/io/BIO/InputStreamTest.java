package com.dugq.io.BIO;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by dugq on 2024/4/9.
 */
public class InputStreamTest {

    @Test
    public void input() throws IOException {
        URL resource = this.getClass().getClassLoader().getResource("text.text");
        String filePath = resource.getFile();
        File file = new File(filePath);
        Path path = file.toPath();


        //字节流
        FileInputStream inputStream = new FileInputStream(filePath);
        inputStream.read(new byte[1024]);

        // 字符流 内部集成了一个streamDecoder，该decoder会读取文件的字节流，然后解码为字符流
        FileReader fileReader = new FileReader(filePath);
        char[] cbuf = new char[1024];
        fileReader.read(cbuf);

        // 缓冲流 类似NIO提供的byteBuffer功能，提供可重复操作而无需重复发生IO。
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        byte[] b1 = new byte[1024];
        //copy次数太多了。
        //首先常规的 内核->本地内存->堆内存 此时数据存于InputStream内部的byte array中
        //然后 inputStream -> bufferedInputStream -> byte[]   byte[]是我们传入的参数
        // 一共进行了4次CPU copy操作
        // 相较于NIO的2次CPU copy，太浪费了。
        bufferedInputStream.read(b1);


        // file NIO
        FileChannel channel =  FileChannel.open(path, StandardOpenOption.WRITE,StandardOpenOption.READ);
        // 内部处理了操作系统read函数调用（从内核复制到直接内存） 以及 从直接内存复制到堆的过程。
        channel.read(ByteBuffer.allocate(1024),1024);
        // 如果使用的是directBuffer，系统就会直接使用开发者构建的buffer，不会再构建临时直接内存区域，用于复制了。
        channel.read(ByteBuffer.allocateDirect(1024));

        long len = file.length();
        // mmap 使用内存映射。 具体如何映射目前还看不明白.
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE,0,1024);
        //映射完成以后，可以直接从mappedBuffer中读取数据了
        byte b = map.get(1);
        System.out.println(b);
        map.load();
        b = map.get(1);
        System.out.println(b);
        map.force();
        channel.close();
        // 网络IO
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("127.0.0.1",9988));
        SocketChannel socketChannel = serverSocket.accept();
        // 内部和file差不多。指不够它不能指定copy的起始位置。
        socketChannel.read(ByteBuffer.allocate(1024));
    }
}
