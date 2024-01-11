package com.dugq.io.buffer;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public class DirectBufferTest {

    /**
     * {@link java.nio.DirectByteBuffer}
     */
    @Test
    public void DirectBuff() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ByteBuffer[] byteBuffers = new ByteBuffer[1026];
        Object clean = null;
        for (int i =0 ;i < 1025; i++){
            try {
                byteBuffers[i] = ByteBuffer.allocateDirect(1024);
            }catch (Throwable e){
                // directBuffer 也是自动回收的。但是它不是GC自动回收的，而是利用虚引用进行回收的。
                // ByteBuffer 对象本身是存在堆中的，其内部保存了直接内存的起始地址和长度
                // 为directBuffer 设置虚引用，再虚引用内部进行回收，利用虚引用的特性，当directBuffer可以销毁时，虚引用也会被销毁

                Method cleaner = byteBuffers[i - 1024].getClass().getDeclaredMethod("cleaner");
                cleaner.setAccessible(true);
                clean = cleaner.invoke(byteBuffers[i - 1024]);
                byteBuffers[i-1024] = null;
                i--;
                System.gc();
            }

        }
        // 虚引用持有的对象已经被回收了。
        Class<?> aClass = clean.getClass();
    }

}
