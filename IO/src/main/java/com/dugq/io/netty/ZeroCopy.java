package com.dugq.io.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by dugq on 2023/7/4.
 */
@Slf4j
public class ZeroCopy {

    /**
     * netty的零copy其实也是应用层零copy的一种解决方案。核心解决的问题是：
     * <li/> 1、jvm缓冲区(直接内存) <-> jvm堆内存区域  之间的复制
     * <li/> 2、对象在堆中的复制导致数据存在多份
     * 具体实现包括：
     * <li/> CompositeByteBuf 将多个buf组合成一个buf，就解决了channel write时只能传入一个buf，所以必须合并buf而带来的复制损耗 {@link #compositeByteBufApi()}
     * <li/> slice & duplicate 浅copy解决 在多线程读同一个buf而必须复制buf而产生的额外损耗 {@link #copy()}
     * <li/> netty socketChannel write 都是使用directBuf，以解决缓冲区copy 到直接内存的损耗 ,还有write buffer
     * <li/> 还有个类似fileChannel的transferTo功能的方法。真正意义上的从读到写的零copy
     */
    public static void main(String[] args) {
        new ZeroCopy().copy();
    }

    /**
     * <h3/>ByteBuf 提供了多种copy方法，但是区别很大。这里进行汇总。也包含了zero-copy的部分
     * <li>为buff创建一个<strong>视图</strong>可以实现zero copy的目的</li>
     *
     * <pre>
     *      +-------------------+------------------+------------------------------------------------+------------------------------------------------+
     *      | 方法                |  内存区域         |     capacity                                  |           referenceCount                        |
     *      |                   |                  |                                                |                                                 |
     *      +-------------------+------------------+------------------------------------------------+------------------------------------------------+
     *      |    copy           |   新开辟的区域      |   可读区域,已读和可写区域不会复制                    |              有自己独有的一份                     |
     *      +-------------------+------------------+------------------------------------------------+------------------------------------------------+
     *      |    duplicate      |   共享原区域       |  完整的映射原buf，已读可写区域都共享                  |         和source共享一个字段                      |
     *      +-------------------+------------------+------------------------------------------------+------------------------------------------------+
     *      | retainedDuplicate |   共享原区域       |  完整的映射原buf，已读可写区域都共享                  |         和source共享一个字段                      |
     *      +-------------------+------------------+------------------------------------------------+------------------------------------------------+
     *      |    slice         |  共享原区域        | 映射原buf的可读区域，已读可写不共享，可以认为是readOnly的 |         和source共享一个字段                      |
     *      +------------------+------------------+-------------------------------------------------+------------------------------------------------+
     *      |  retainedSlice    |  共享原区域       | 映射原buf的可读区域，已读可写不共享，可以认为是readOnly的 |         和source共享一个字段                      |
     *      +-------------------+------------------+------------------------------------------------+------------------------------------------------+
     *      |  readSlice       |  共享原区域       | 映射原buf的指定数量的可读区域，已读可写不共享，            |         和source共享一个字段                      |
     *      |                  |                 | 可以认为是readOnly的。它会改变原buf的readIndex        |                                                 |
     *      +-------------------+------------------+------------------------------------------------+------------------------------------------------+
     * </pre>
     */
    public void copy(){
        ByteBuf source = Unpooled.buffer(1024);
        source.writeBytes("hello world".getBytes());
        source.readByte();
        source.readByte();
        ByteBuf slice = source.slice();
        ByteBuf copyBuf = source.copy();
        ByteBuf duplicate = source.duplicate();
        ByteBuf retainedDuplicate = source.retainedDuplicate();
        ByteBuf retainedSlice = source.retainedSlice();


        log.info("modify source byte index at 1");
        source.setByte(3, (byte) 'a');

        // copy 和 slice 复制的是可读区域，而原buf已读了2个字节，所以下下一个可读字节： 原buf 和 duplicate buf都是3，而copy 和 slice都是1
        log.info("source:readBytes={},readIndex = {} writeIndex = {}  capacity = {} limit = {} source buffer index 1 byte = {}",source.readableBytes(),source.readerIndex(),source.writableBytes(),source.capacity(),source.writableBytes(),(char)source.getByte(3));
        log.info("copyBuf:readBytes={},readIndex = {} writeIndex = {}  capacity = {} limit = {} copy buffer index 1 byte = {}",copyBuf.readableBytes(),copyBuf.readerIndex(),copyBuf.writableBytes(),copyBuf.capacity(),copyBuf.writableBytes(),(char)copyBuf.getByte(1));
        log.info("slice:readBytes={},readIndex = {} writeIndex = {}  capacity = {} limit = {} slice buffer index 1 byte = {}",slice.readableBytes(),slice.readerIndex(),slice.writableBytes(),slice.capacity(),slice.writableBytes(),(char)slice.getByte(1));
        log.info("duplicate:readBytes={},readIndex = {} writeIndex = {} capacity = {} limit = {} duplicate buffer index 1 byte = {}",duplicate.readableBytes(),duplicate.readerIndex(),duplicate.writableBytes(),duplicate.capacity(),duplicate.writableBytes(),(char)duplicate.getByte(3));
        log.info("retainedDuplicate:readBytes={},readIndex = {} writeIndex = {} capacity = {} limit = {} retainedDuplicate buffer index 1 byte = {}",retainedDuplicate.readableBytes(),retainedDuplicate.readerIndex(),retainedDuplicate.writableBytes(),retainedDuplicate.capacity(),retainedDuplicate.writableBytes(),(char)retainedDuplicate.getByte(3));
        log.info("retainedSlice:readBytes={},readIndex = {} writeIndex = {} capacity = {} limit = {} retainedSlice buffer index 1 byte = {}",retainedSlice.readableBytes(),retainedSlice.readerIndex(),retainedSlice.writableBytes(),retainedSlice.capacity(),retainedSlice.writableBytes(),(char)retainedSlice.getByte(1));

        /*
         *
         *  source:   capacity = 1024  readIndex = 2   writeIndex = 1022    readableBytes=9      writableBytes = 1013    a
         *  copy:     capacity = 9     readIndex = 0   writeIndex = 0       readableBytes=9      writableBytes = 0       l
         *  slice     capacity = 9     readIndex = 0   writeIndex = 0       readableBytes=9      writableBytes = 0       a
         * duplicate  capacity = 1024  readIndex = 2   writeIndex = 1013    readableBytes=9      writableBytes = 1013    a
         * 分析：
         * 1、 slice 和 duplicate 都是浅copy，内存映射过去的，而copy是把原buf的可读区域复制到新的buf中。新的buf raedIndex = writeIndex = writeableIndex = 0
         * 2、slice 和 duplicate 的区别在于slice相当于一个readOnly数据区域，把原buf的可读区域映射过去了。而duplicate相当于一个分身，和原buf一模一样的，都可以对buf进行读写操作。
         * 3、场景：
         *  copy： 复制一份新的buf，包含原buf的可读区域
         *  slice： 映射原buf的可读区域，可以对其进行读操作，但是不能对其进行写操作。
         *  duplicate： 映射原buf的全部区域，这个方法理论来说存在线程安全的问题，场景暂未想到。
         *
         * retainedSlice 和 retainedDuplicate 与 slice 和 duplicate 在内存上一样。后续在测试他们在引用计数上的差别。
         */

        log.info("source referenceCount = {} copy referenceCount = {} slice referenceCount = {} duplicate referenceCount = {} retainedSlice referenceCount = {} retainedDuplicate referenceCount = {}",source.refCnt(),copyBuf.refCnt(),slice.refCnt(),retainedDuplicate.refCnt(),retainedSlice.refCnt(),retainedDuplicate.refCnt());
        source.release();
        log.info("source referenceCount = {} copy referenceCount = {} slice referenceCount = {} duplicate referenceCount = {} retainedSlice referenceCount = {} retainedDuplicate referenceCount = {}",source.refCnt(),copyBuf.refCnt(),slice.refCnt(),retainedDuplicate.refCnt(),retainedSlice.refCnt(),retainedDuplicate.refCnt());
        source.release();
        log.info("source referenceCount = {} copy referenceCount = {} slice referenceCount = {} duplicate referenceCount = {} retainedSlice referenceCount = {} retainedDuplicate referenceCount = {}",source.refCnt(),copyBuf.refCnt(),slice.refCnt(),retainedDuplicate.refCnt(),retainedSlice.refCnt(),retainedDuplicate.refCnt());
        /*
        source referenceCount = 3 copy referenceCount = 1 slice referenceCount = 3 duplicate referenceCount = 3 retainedSlice referenceCount = 3  retainedDuplicate referenceCount = 3
        source referenceCount = 2 copy referenceCount = 1 slice referenceCount = 2 duplicate referenceCount = 2 retainedSlice referenceCount = 2  retainedDuplicate referenceCount = 2
        source referenceCount = 1 copy referenceCount = 1 slice referenceCount = 1 duplicate referenceCount = 1 retainedSlice referenceCount = 1  retainedDuplicate referenceCount = 1

        分析：
         retainedSlice 和 retainedDuplicate 会增加source buf的引用计数，而slice 和 duplicate不会。
         copy 内存独立，和source 完全无关
         当source release之后，slice 和 duplicate 和 retainedSlice 和 retainedDuplicate 都是同步的。
        */

        log.info("source before read slice: readIndex = {} ",source.readerIndex());
        ByteBuf readSlice = source.readSlice(2);
        log.info("source after read slice:readIndex = {} ",source.readerIndex());
        log.info("readSlice:readIndex = {} capacity={}",readSlice.readerIndex(),readSlice.capacity());
    }

    public void compositeByteBufApi(){
        CompositeByteBuf byteBuf = Unpooled.compositeBuffer();
        ByteBuf source1 = Unpooled.buffer(128);
        source1.writeBytes("hello world".getBytes());
        ByteBuf source2 = Unpooled.buffer(1024);
        source2.writeBytes("world hello ".getBytes());
    }


}
