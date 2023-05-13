package com.example.learn.jreApi.base.unsafe;

import com.example.util.StringNumberUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.openjdk.jol.vm.VM;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.ArrayList;
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


        /**
         *  CAS 操作
         * @param o         包含要修改field的对象
         * @param offset    对象中某field的偏移量
         * @param expected  期望值
         * @param update    更新值
         * @return          true | false
         */
        public final native boolean compareAndSwapObject(Object o, long offset,  Object expected, Object update);
        public final native boolean compareAndSwapInt(Object o, long offset, int expected,int update);
        public final native boolean compareAndSwapLong(Object o, long offset, long expected, long update);


        /**
         * 线程调度
         * 这部分，包括线程挂起、恢复、锁机制等方法。
         */
        //取消阻塞线程
        public native void unpark(Object thread);
        //阻塞线程
        public native void park(boolean isAbsolute, long time);
        //获得对象锁（可重入锁）
        @Deprecated
        public native void monitorEnter(Object o);
        //释放对象锁
        @Deprecated
        public native void monitorExit(Object o);
        //尝试获取对象锁
        @Deprecated
        public native boolean tryMonitorEnter(Object o);


        /**
         * 类相关
         * 此部分主要提供Class和它的静态字段的操作相关方法，包含静态字段内存定位、定义类、定义匿名类、检验&确保初始化等。
         */
        //获取给定静态字段的内存地址偏移量，这个值对于给定的字段是唯一且固定不变的
        public native long staticFieldOffset(Field f);
        //获取一个静态类中给定字段的对象指针
        public native Object staticFieldBase(Field f);
        //判断是否需要初始化一个类，通常在获取一个类的静态属性的时候（因为一个类如果没初始化，它的静态属性也不会初始化）使用。 当且仅当ensureClassInitialized方法不生效时返回false。
        public native boolean shouldBeInitialized(Class<?> c);
        //检测给定的类是否已经初始化。通常在获取一个类的静态属性的时候（因为一个类如果没初始化，它的静态属性也不会初始化）使用。
        public native void ensureClassInitialized(Class<?> c);
        //定义一个类，此方法会跳过JVM的所有安全检查，默认情况下，ClassLoader（类加载器）和ProtectionDomain（保护域）实例来源于调用者
        public native Class<?> defineClass(String name, byte[] b, int off, int len, ClassLoader loader, ProtectionDomain protectionDomain);
        //定义一个匿名类
        public native Class<?> defineAnonymousClass(Class<?> hostClass, byte[] data, Object[] cpPatches);


        /**
         * 对象操作
         */
        //返回对象成员属性在内存地址相对于此对象的内存地址的偏移量
        public native long objectFieldOffset(Field f);
        //获得给定对象的指定地址偏移量的值，与此类似操作还有：getInt，getDouble，getLong，getChar等

        //从对象的指定偏移量处获取变量的引用，使用volatile的加载语义
        public native Object getObjectVolatile(Object o, long offset);
        //存储变量的引用到对象的指定的偏移量处，使用volatile的存储语义
        public native void putObjectVolatile(Object o, long offset, Object x);
        //有序、延迟版本的putObjectVolatile方法，不保证值的改变被其他线程立即看到。只有在field被volatile修饰符修饰时有效
        public native void putOrderedObject(Object o, long offset, Object x);
        //绕过构造方法、初始化代码来创建对象
        public native Object allocateInstance(Class<?> cls) throws InstantiationException;

        /**
         * 数组相关
         */
        //返回数组中第一个元素的偏移地址
        public native int arrayBaseOffset(Class<?> arrayClass);
        //返回数组中一个元素占用的大小
        public native int arrayIndexScale(Class<?> arrayClass);


        /**
         * 内存屏障
         */
        //内存屏障，禁止load操作重排序。屏障前的load操作不能被重排序到屏障后，屏障后的load操作不能被重排序到屏障前
        public native void loadFence();
        //内存屏障，禁止store操作重排序。屏障前的store操作不能被重排序到屏障后，屏障后的store操作不能被重排序到屏障前
        public native void storeFence();
        //内存屏障，禁止load、store操作重排序
        public native void fullFence();


        /**
         * 系统相关
         */
        //返回系统指针的大小。返回值为4（32位系统）或 8（64位系统）。
        public native int addressSize();
        //内存页的大小，此值为2的幂次方。
        public native int pageSize();

    }

    @Test
    public void testSystem(){
        Unsafe unsafe = reflectGetUnsafe();
        int i = unsafe.addressSize();
        System.out.println(i);
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
//        List<Byte> byteList =StringNumberUtil.getByte(20221122L);
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
//        long result = StringNumberUtil.byte2Long(Arrays.asList(byte1, byte2, byte3, byte4));
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
        // jol
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
        return StringNumberUtil.byte2Long(bytes);
    }

    private static long getAddress(Long address){
        Unsafe unsafe = reflectGetUnsafe();
        List<Byte> bytes = new ArrayList<>();
        for (int i=0; i<8;i++){
            bytes.add(unsafe.getByte(address+i));
        }
        return StringNumberUtil.byte2Long(bytes);
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
