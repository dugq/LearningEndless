# 简介
* Unsafe是位于sun.misc包下的一个类，主要提供一些用于执行低级别、不安全操作的方法，如直接访问系统内存资源、自主管理内存资源等。
* 这些方法在提升Java运行效率、增强Java语言底层资源操作能力方面起到了很大的作用。
* 但由于Unsafe类使Java语言拥有了类似C语言指针一样操作内存空间的能力，这无疑也增加了程序发生相关指针问题的风险。
* 在程序中过度、不正确使用Unsafe类会使得程序出错的概率变大，使得Java这种安全的语言变得不再“安全”，因此对Unsafe的使用一定要慎重。
* Unsafe类为一单例实现，提供静态方法getUnsafe获取Unsafe实例，当且仅当调用getUnsafe方法的类为引导类加载器所加载时才合法，否则抛出SecurityException异常。
* [技术文章](https://tech.meituan.com/2019/02/14/talk-about-java-magic-class-unsafe.html)
# 获取使用
* 通过Java命令行命令-Xbootclasspath/a把调用Unsafe相关方法的类A所在jar包路径追加到默认的bootstrap路径中，使得A被引导类加载器加载，从而通过Unsafe.getUnsafe方法安全的获取Unsafe实例。
        
~~~java
 java -Xbootclasspath/a: ${path}   // 其中path为调用Unsafe相关方法的类所在jar包路径
~~~

* 通过反射获取单例对象theUnsafe。
~~~java
 public class test{
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
~~~

# 功能
![img.png](f182555953e29cec76497ebaec526fd1297846.png)

## 直接操作内存
 [Unsafe](UnSafeTest.java)
* 可根据对象+便宜量操作属性
* 可根据内存地址直接操作内存
* 内存操作不当会导致JVM进程直接结束

## CAS
* CAS是一条CPU的原子指令（cmpxchg指令），不会造成所谓的数据不一致问题，Unsafe提供的CAS方法（如compareAndSwapXXX）底层实现即为CPU指令cmpxchg。


## 线程调度
* LockSupport.park() 和 LockSupport.unpark()的实现原理

## class相关
* Lambda 表达的式的相关实现原理

## 对象操作
* 可以绕过构造方法创建对象

## 数组
* AtomicIntegerArray 实现依赖这些方法定位数组中元素位置

## 内存屏障
* StampedLock 的实现原理

## 系统相关
* java.nio Bits
