package com.dugq.io.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.unix.Buffer;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * Created by dugq on 2023/6/20.
 */
public class ByteBufferVSByteBufApi {

    public static void main(String[] args) throws IOException {
        ByteBufferVSByteBufApi api = new ByteBufferVSByteBufApi();
        //api.byteBufferApi();
        api.byteBufApi(api.byteBufCreate());
    }

    /**
     *  0 <= mark <= position <= limit <=   capacity <br/>
     * [----------------------------------------]    <br/>
     *
     * <li/> mark : 相当于记忆点 初始值 为 -1 但是负数是非法值，表示未设置。 mark的有效范围 【 0 , position】超出将被视作无效操作
     * <li/> position : read / write 当前索引。 可配置参数，有效范围: 【0，limit】
     * <li/> limit : 限制 最大可读/可写索引    自定义初始化值，ByteBuffer静态工厂方法设定默认值 = capacity，即所有区域都可读写
     * <li/> capacity : buffer容量，即数组大小
     *
     * <li/> 在构造之初字节流区域的所有元素都被设置为0。所以不存在空指针的情况
     * <li/> buffer是可读可写的字节流，但是mark / position /limit 只有一个属性，整个buffer的设计是线程不安全的，所以读写切换必须要修改mark/position/limit的值，详见test中的api
     */
    public void byteBufferApi(){
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        ByteBuffer allocate = ByteBuffer.allocate(20);


        //打标
        buffer.mark();
        //将position重置到mark。注意如果在mark和reset之间有操作，小心mark被丢弃哦
        buffer.reset();

        // 将limit设置为当前position，再将position重置为0，并失效mark。 一般用于写读切换。
        buffer.flip();
        //同上，但不会修改limit。上述方法不能连续调用，不然第二次limit就等于0了，buffer就毁了。
        buffer.rewind();

        //计算可用/未读长度。 limit-position。写模式时，该方法返回可写区域，读模式时该方法返回未读长度.注意它不是容量哦，
        buffer.remaining();
        //是否还能读/写 position < limit
        buffer.hasRemaining();
        // 这个才是容量。类似 list 的size 方法，数组的length
        buffer.capacity();

        //重置buffer，将mark,position,limit重置为初始化状态
        buffer.clear();

        //压缩buffer，其实就是把已读区域删掉，以达到节省空间的目的
        buffer.compact();

        //顺序读写,内部会移动position的位置
        buffer.put((byte)1);
        buffer.put(new byte[]{0,1});
        buffer.get();
        buffer.get(new byte[10]);

        // 随机读写，不会移动position的位置
        buffer.put(1,(byte) 1);
        buffer.get(1);

        // 浅copy，小心使用
        buffer.slice();

        //directBuffer 不是基于数组实现，不能直接获取到到数组
        //存于堆的字节流都是基于字节数组实现的，可以直接获取到
        buffer.array();


    }


    public void byteBufApi(ByteBuf buf){
        //对标 buffer.mark() & buffer.reset();
        buf.markReaderIndex();
        buf.markWriterIndex();
        buf.resetReaderIndex();
        buf.resetWriterIndex();

        //容量 vs buffer.capacity()
        int capacity = buf.capacity();
        //读写索引 buf.position()
        buf.readerIndex();
        buf.writerIndex();

        // 可读/可写长度 vs buf.remaining()
        buf.writableBytes();
        buf.readByte();

        //是否可读/可写 vs buf.hasRemaining()
        buf.isReadable();
        buf.isWritable();

        // buffer.clear();
        buf.clear();

        buf.array();
        //注意此方法返回false 不一定是directBuf，也可能是compos
        buf.hasArray();
        //第一个有数据位置的偏移量
        buf.arrayOffset();

        //buf.set**
        //buf.get**
        //可太多了，支持各种数据类型
        buf.setBytes(1,new byte[]{1});
        buf.getByte(1);

        //set/get 是不会移动index的，而write 和 read会自动移动位置
        buf.writeByte((byte)1);
        buf.readByte();

        // 这组方法是buf独有的，其主要目的是针对直接内存的回收
        // netty 内部实现了自己的垃圾回收策略。当 属性 refcnt >0时，对象是不会被回收的，当refcnt <= 0 时内存就会被回收
        // 具体回收的方式也会因具体的实现方式而不同。
        // 针对于池化方式的buf，内存是不会真的被回收的，而是被重新利用了。
        // 针对非池化的buf，heapBuf会将数组引用清空，对象在失去引用后，下次GC时自动回收
        //              而directBuf，大概率像jdk一样利用cleaner虚引用进行清理
        buf.retain();
        buf.release();

        //对标 buffer.slice 都是浅copy 只是浅的层次不同。返回新的buf最好不要写。虽然你有办法写。
        buf.slice();
        buf.duplicate();
        //实际中，可能这两会使用更多。 因为如果源对象被回收了，新对象可能会发生一些不可预估的事情。
        buf.retainedDuplicate();
        buf.retainedSlice();

        System.out.println();
    }



    public void testIO() throws IOException {
        FileChannel channel = FileChannel.open(new File("/Users/duguoqing/dev/myWP/SpringBootDemo/src/main/resources/config/test.txt").toPath(), StandardOpenOption.WRITE,StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        /*
          内部调用 buffer.put(ByteBuffer);
          而put(ByteBuffer)实现依赖循环put(int,byte)
          所以该方法不会对mark,position,limit做任何改动
         */
        channel.read(buffer);
        channel.position(0);
        // 从position 开始写 buffer.remaining()个字节，其他不变
        channel.write(ByteBuffer.wrap("我是谁".getBytes()));
    }

    public ByteBuf byteBufCreate(){
        // 通过netty 生成默认的byteBuf，是否池化可通过启动参数配置
        ByteBuf defaultBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        // 自定义池化字节流
        ByteBuf pooledHeapBuf = PooledByteBufAllocator.DEFAULT.heapBuffer();
        ByteBuf pooledDirectBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
        // 自定义非池化字节流
        ByteBuf unpooledHeapBuf = UnpooledByteBufAllocator.DEFAULT.heapBuffer();
        ByteBuf unpooledDirectBuf = UnpooledByteBufAllocator.DEFAULT.directBuffer();
        // 给非netty项目使用的
        ByteBuffer directBuf = Buffer.allocateDirectWithNativeOrder(10);
        // 在netty服务中可以使用channel装配的默认生成器生成字节流
        Channel channel = new NioServerSocketChannel();
        ByteBufAllocator alloc = channel.alloc();
        return pooledHeapBuf;
    }

    @Test
    public void testDirectBuf(){
        ByteBuf pooledDirectBuf = PooledByteBufAllocator.DEFAULT.directBuffer(1024);
        pooledDirectBuf.writeBytes("这是一段测试语言".getBytes());
        int oldSize = pooledDirectBuf.readableBytes();
        System.out.println("size = "+oldSize);
        while (pooledDirectBuf.readableBytes()>0){
            byte b = pooledDirectBuf.readByte();
            int i = RandomUtils.nextInt(0, 10);
        }
    }

    @Test
    public void testDirectClean(){
//        ByteBuf pooledDirectBuf = PooledByteBufAllocator.DEFAULT.directBuffer(1024);
//        PooledByteBufAllocator.PoolThreadLocalCache;
//        PooledByteBufAllocator.PoolArena;
//        PoolSubpage;//队列结构
//        PoolChunk;
//        PoolChunkList; //队列结构
//        PoolChunk;




    }

}

