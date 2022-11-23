package com.example.learn.jre.thread.unsafe;

import com.example.util.ThreadUtil;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openjdk.jol.vm.VM;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class UnSafeTest {

    public abstract class sourceUnsafe{
        /**
         * 内存操作
         * {@link #testMemory}
         * {@link #testOpMemory}
         */
        //分配内存, 相当于C++的malloc函数
        public native long allocateMemory(long bytes);
        //扩充内存
        public native long reallocateMemory(long address, long bytes);
        //释放内存
        public native void freeMemory(long address);
        //在给定的内存块中设置值
        public native void setMemory(Object o, long offset, long bytes, byte value);
        //内存拷贝
        public native void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes);
        //获取给定地址值，忽略修饰限定符的访问限制。与此类似操作还有: getInt，getDouble，getLong，getChar等
        public native Object getObject(Object o, long offset);
        //为给定地址设置值，忽略修饰限定符的访问限制，与此类似操作还有: putInt,putDouble，putLong，putChar等
        public native void putObject(Object o, long offset, Object x);
        //获取给定地址的byte类型的值（当且仅当该内存地址为allocateMemory分配时，此方法结果为确定的）
        public native byte getByte(long address);
        //为给定地址设置byte类型的值（当且仅当该内存地址为allocateMemory分配时，此方法结果才是确定的）
        public native void putByte(long address, byte x);



    }

    public void testMemory() throws NoSuchFieldException {
        User user = new User();
        Field field = User.class.getDeclaredField("age");

        Unsafe unsafe = reflectGetUnsafe();
        //分配内存, 相当于C++的malloc函数
        long l = unsafe.allocateMemory(10);
        //扩充内存
        unsafe.reallocateMemory(l,10);
        //释放内存
        unsafe.freeMemory(l);

        //获取指定字段的偏移量
        long offset = unsafe.objectFieldOffset(field);
        //在给定的内存块中设置值
        unsafe.setMemory(user,offset, 4L,(byte)1);
        //内存拷贝
        unsafe.copyMemory(null,-1L,new Integer(2),-1,32);
        //获取给定地址值，忽略修饰限定符的访问限制。与此类似操作还有: getInt，getDouble，getLong，getChar等
        unsafe.getObject(user,offset);
        //为给定地址设置值，忽略修饰限定符的访问限制，与此类似操作还有: putInt,putDouble，putLong，putChar等
        unsafe.putObject(user, offset,1);
        //获取给定地址的byte类型的值（当且仅当该内存地址为allocateMemory分配时，此方法结果为确定的）
        byte aByte = unsafe.getByte(l);
        //为给定地址设置byte类型的值（当且仅当该内存地址为allocateMemory分配时，此方法结果才是确定的）
        unsafe.putByte(l,(byte)1);
    }


    @Test
    public void testOpMemory() throws NoSuchFieldException {
        User user = new User();
        Field field = User.class.getDeclaredField("age");
        Unsafe unsafe = reflectGetUnsafe();
        long offset = unsafe.objectFieldOffset(field);

        /**
         *  setMemory(Object o, long offset, long bytes, byte value) 是按字节设置值，每次只能设置一个字节,因为byte强转后，超过127的部分会益处，忽略。设置多个字节，多个字节的值是相同的
         */
//        byte b = (byte) 127;
//        unsafe.setMemory(user,offset,1L, b);
//        System.out.println("byte="+b+" "+user.getAge());

        /**
         * setMemory(Object o, long offset, long bytes, byte value) 设置int值，需要按照低4位到高4位倒序设置
         */
//        List<Byte> byteList =ThreadUtil.getByte(20221122L);
//        for (int i =0 ;i < byteList.size(); i++) {
//            Byte b = byteList.get(i);
//            unsafe.setMemory(user,offset+i,1L, b);
//        }
//        System.out.println(user.getAge());

        /**
         * 设置的值再获取回来
         */
//        user.setAge(20221122);
//        byte byte1 = unsafe.getByte(user, offset);
//        byte byte2 = unsafe.getByte(user, offset+1);
//        byte byte3 = unsafe.getByte(user, offset+2);
//        byte byte4 = unsafe.getByte(user, offset+3);
//        long result = ThreadUtil.byte2Long(Arrays.asList(byte1, byte2, byte3, byte4));
//        System.out.println(result);
        /**
         * 看看内存的情况
         * 经过一天的测试，疯了，哎。内存地址 会 更根据环境和压缩配置 变更位数，压缩的场景也不确定
         */
        Field nameFiled = User.class.getDeclaredField("name");
        long nameOffset = unsafe.objectFieldOffset(nameFiled);
        int anInt = unsafe.getInt(user, nameOffset);
        System.out.println("source int ----------"+anInt);

        long NameObjAddress = getAddress(user, nameOffset);
        System.out.println("address long -----" + NameObjAddress);
        System.out.println("address long left 3 -----" +( NameObjAddress<<3));
        long l = VM.current().addressOf(user.getName());
        System.out.println("正确的值 ----------"+l);
        System.out.println("binary parse long ---"+Long.parseLong("11101101100111011111000111110010",2));
//        Field value = String.class.getDeclaredField("value");
//        long nameValuesOffset = unsafe.objectFieldOffset(value);
//        long nameStrValueArrayAddress = getAddress(NameObjAddress + nameValuesOffset);
//        int arrayBaseOffset = unsafe.arrayBaseOffset(char[].class);
//        System.out.println(unsafe.getChar(nameStrValueArrayAddress+arrayBaseOffset));

    }


    private static long getAddress(Object o,Long offset){
        Unsafe unsafe = reflectGetUnsafe();
        List<Byte> bytes = new ArrayList<>();
        for (int i=0; i<4;i++){
            bytes.add(unsafe.getByte(o, offset+i));
        }
        return ThreadUtil.byte2Long(bytes);
    }

    private static long getAddress(Long address){
        Unsafe unsafe = reflectGetUnsafe();
        List<Byte> bytes = new ArrayList<>();
        for (int i=0; i<8;i++){
            bytes.add(unsafe.getByte(address+i));
        }
        return ThreadUtil.byte2Long(bytes);
    }

    @Setter
    @Getter
    private static class User{

        private int age;

        private String name ="dugq";
    }

    private static Unsafe reflectGetUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
