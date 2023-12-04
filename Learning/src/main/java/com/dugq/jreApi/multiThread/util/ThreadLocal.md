# ThreadLocal
* java线程通信采用的是内存共享，但如何想要实现内存隔离，就需要使用到ThreadLocal
[测试用例](ThreadLocal.md)
## 原理
`在每个线程内部保存一份变量副本。`
1. ThreadLocal类的功能
   1. ThreadLocal本身只是一个标记，作为ThreadLocalMap的key。 这很重要，给开发这一种多线程共享对象的假象。实际上操作的是线程各自ThreadLocalMap的value哦
   2. 向外暴露ThreadLocalMap的功能方法。 和 上一点搭配，一起哄骗开发者，给开发这一种多线程共享对象的假象。
   3. 负责维护一个 唯一hash值。这对ThreadLocalMap很重要，因为ThreadLocalMap不像HashMap那么复杂。ThreadLocalMap要的是效率。
2. 变量存储在 ThreadLocalMap类中。
   1. ThreadLocalMap 类似hashMap。但它不存在hash冲突，所以每个桶上只存储一个元素。当hash碰撞时，就动态移动hash指针，或者扩容
   2. Entry 继承 weakReference，ThreadLocal对象作为key存储在weakReference的reference中，是弱引用
   3. threadLocal的值存储在Entry的私有变量value上，是强应用
   4. ` 所以：当threadLocal对象没有强引用时，entry的key可能被清理，但value不会被清理。且由于value仍然持有应用，所以其对象不能算做可回收对象。所以即使栈帧已经运行完毕，value依然无法被GC。` 这就是可能发生内存溢出的原由
   5. `key为null的vale，在下次ThreadLocal.set 且发生指针碰撞时，才会触发清理。` 此时，value才会被GC认为是可回收对象。
   6. 这里有个疑问：为什么不把key和value再封装一下，整体存入weakReference呢？
    * 1、弱引用的特性：是当对象没有强引用时，对象可能被GC。
    * 2、threadLocal使用习惯： threadLocal对象通常被定义为静态或者私有变量，所以它丢失强引用被回收的概率很小。而如果value是弱引用的话，那它被回收的概率就很大，从而丢失了它原本的作用。假设value是弱引用，看下面代码：
~~~java 
  public class test(){
    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    public static void main(String[] args){
        function1();
        System.gc();
        //如果value是弱引用的话，最后打印结果一定是null ！！！ 而ThreadLocal通常都是这么用的。
        function2();
    }
    public void function1(){
        threadLocal.set("test");
    }
    public void function2(){
        System.out.println(threadLocal.get());
    }
}

~~~
3. ThreadLocalMap对象在Thread中定义
   1. TreadLocalMap的方法不需要保证线程安全，因为每个线程只能操作自己的ThreadLocalMap，所以ThreadLocalMap一定是线程安全的。
   2. ThreadLocal的所有方法也不需要保证线程安全了，因为它是通过Thread.currentThread.getThreadLocalMap 映射到自己的ThreadLocalMap上进行操作的。 
