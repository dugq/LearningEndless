# 并发
### 并发的由来及其引入的问题
* 提高CPU的利用率
  * 单核上，减少IO时CPU的空闲时间，尤其对于IO密集型程序提升巨大
  * 多CPU时代，启动多个线程让程序最大限度的并发执行，以达到让程序更快运行的目的
* 为了提高CPU的利用率，现代计算机架构还有很多解决方案
  * 1、操作系统层面时分复用。
    * 优点1:当某个线程在等待IO时可主动/被动 让出CPU占用，减少CPU空闲时间
    * 优点2:该方案可以让多进程（线程）在单CPU的场景下看起来像是并发执行
    * 引入问题1: 线程主动/被动让出CPU，必然牵涉上下文切换，产生额外损耗
  * 2、指令流水线 和 指令重排序
    * 优点： 大大提高了机器码指令的执行速度，提高了CPU的效率
    * 缺点： 引入了顺序性问题
  * 3、多核心设计 和 高速缓冲区
    * 优点1：高速缓冲区减少了在CPU计算的过程中读写主存致使CPU空闲的时间
    * 优点2: 多核心才能让多进程/线程真正实现并发
    * 引入问题1: 可见性问题（内存一致性问题）
    * 引入问题2: 原子性问题。多核心场景下，任何非原子性的操作都不具有线程安全性
* 针对于java，其本身为了让编程更简单，语言更高级，也会引入一些问题
  * 1、有顺性问题。代码优化而产生的
  * 2、原子性问题。这就不用说了，各类语法糖、各类内存屏障、各类监控


### 并发的应用
#### 线程基础
* 进程是系统资源分配的最小单位，而线程作为系统调用的最小单位。
* 线程状态包括： [测试类](ThreadStatusTest.java)
                  
~~~code
                       start()              run执行结束
             NEW -------------> runnable --------------------> stop     NEW 和 STOP是无法使用Jstack监控到的  
                                   ^
                                   |
                                   |     synchorized
                                   | ------------------> blocking  
                                   |
                                   | lock wait condition lockSupport
                      sleep        |   依赖unsafe.park实现  
 time-waiting<-------------------------------------------> waiting
~~~
* 线程中断interrupt：
  1. Thread.interrupt 和 Thread.isInterrupted是一对方法
  2. Thread.interrupt 并不会直接中断线程，该方法内部分为两步：
     * 将JVM的osThread线程状态变更为1，即中断状态
     * 将支持中断的挂起状态线程唤醒，比如：Object.wait、LockSupport.park、Thread.sleep等
* Thread的本地方法实现：
  * Thread.c文件声明了本地方法的具体实现
  * JVM.cpp中等于了Thread实现的抽象
  * OSThread.hpp 中具体定义了不同操作系统中Thread的实现
### 并发中的难题
* 上下文切换带来的消耗
    * 
* 死锁问题
    * 造成死锁的必要条件
      * `多个线程`对`多个共享资源`按照`不同顺序` `没有超时`的访问
    * 减少一个线程同时获取多个资源锁
    * 避免在锁内占用多个资源
    * 使用超时锁代替永久锁（这种方式可能会引发其他问题）
    * 总结： 细化锁的粒度，减少锁中资源的占用
* 硬件、软件方面的资源竞争
    * 硬件问题可以通过增加物理资源解决，比如集群
    * 软件限制可以采用资源复用
* 代码层面上的三要素：
  * 原子性
  * 可见性
  * 有序性


### 关键词
* 上下文切换
   * 当线程暂停运行退出CPU资源时，需要讲当前线程的运行状态保存起来，以便下一次运行。从任务的保存到再加载的过程就叫上下文的切换。
   * 在程序的运行时，上下文的切换实际上是一种资源的浪费，它本身也就暂用CPU等硬件资源   
* 线程安全
  * 这是一个形容词，其主语是某个存在与主存中的对象
  * 其场景是：
    * 在**多个线程**同时对该对象进行一个或者一系列操作时，
    * 在不用考虑这些在线程在运行时环境下的**调度和交替执行**，
    * 也不需要进行额外的同步，
    * 调用方不用进行任何其他协调操作
  * 得出结论：
    * 调用这个对象的行为都可以获取到正确的结果

### [性能检测工具](../../os/monitoring/性能检测工具.md)
 
  
## 线程间通信和同步
* 通信： 线程间的数据交换    
* 同步： 不同线程对同一操作发生的相对顺序控制
* Java采用的内存共享模型。通过共享内存中的数据交换数据，通过并发控制达到同步的效果
* [线程通信详解](线程通信.md)

### 可见性问题
* JMM中，线程间通过访问共享内存以达到线程间通信的目的
* 但是由于线程内本地变量（缓冲区内）何时刷新到主存且何时更新其他线程缓冲区就是个问题。也就是我们常说的可见性问题

##### 原因
* 1、CPU流水线设计、CPU写缓冲设计等导致线程本地数据延时刷新到主存 、 主存数据提前缓存至线程本地缓存
* 2、指令重排序导致引起指令执行顺序与程序员预期不符 ![](resource/codeResort.png)
* 总结： 顺序一致性模型是理想状态，如果严格执行，那CPU的高速缓存意义就不大了，并发的收益也将大大缩减，另外各类重排序优化必然不能发生

##### JVM本身的解决方案
* 1、JVM 自身通过内存屏障遵守happen-before规则和as-if-serial规则，以实现程序尽量按照程序员的想法进行
    * as-if-serial ： 不管怎么重排序，单线程的最终结果一定是正确的
    * happen-before ： JVM通过一些特殊处理，保证了一些基本的功能的顺序，比如加锁必然发生在同步代码前    
* 2、JMM保证经过经过同步处理的程序具有严格的顺序一致性
* 3、未经过同步处理的线程在执行时，其读取的值要么是其他线程提前写回的，要么是初始化的默认值(null,0,false)

##### 程序员的解决方案
在大部分情况下，多线程做做的事情都是互不相干的，他们不需要经过特意的同步处理，只有在存在数据竞争或者依赖关系的时候，才需要程序员特别注意。
而这，JVM是不会明白如何处理的，但是它提供了一系列的方法供我们驱使：
* volatile关键字
    * JVM通过插入内存屏障禁止了volatile变量读写的重排序
    * volatile变量本身的操作使用LOCK#开头的CPU指令修饰，以保证对于变量的修改会及时刷回主存，并通知其他线程变量个已修改
    * volatile变量每次读也会从主存读取数据。这个说法，不知道成不成立。因为存在也合理，但每次都从主存读取数据，有点浪费了吧，人家说不定不常变更呢？但无所谓了，相比来说，它已经是最轻量级的方案了
    * volatile 变量还会限制指令重排序，避免变量的操作顺序被改变[测试方法](VolatileTest.java)
    * 总结 ： volatile关键字 通过只是保证了单个变量的读写操作具有原子性，解决的是最基本的可见性问题。
* final关键字。在构造函数中初始化的final对象，在其他线程中读取到的值是最新的
  * 通常情况下在构造函数中初始化的属性都定义为final类型，在构造的同时被其他线程读取后，其值必然完成初始化，如果特殊情况下不能定义为final属性，则在并发场景下，需要先进行判空处理。
* 加锁 [LOCK详解](../lock/readme.md)
    * synchronized 关键字 
    * LOCK 接口及 LockSupport工具类    
    
##### 案例
 * 双重检测规则。对象的初始化和引用赋值的重排序
 
 
 
 
### 原子性问题
原子性问题看起来更像是顺序一致性的细化问题。顺序一致性说的是什么顺序呢？是以什么作为单位呢？
个人觉得，所谓顺序一致性研究的就是具有原子性的一系列列操作的顺序问题。反之，具有原子性的一个或者一系列操作，在本层次就不需要考虑它内部的顺序性问题。至于其内部的顺序性那是下层次的事情
比如说i++，在Java代码里，它是一条指令，然而在CPU指令里，它确实两条指令，那可以把它当成一个单位去和其他指令进行排序吗？肯定是不能的
但是AtomicInteger.increase()在java里也是一条指令，翻译成CPU指令，那可不止一两条指令了。但是它却可以。

* [原子操作类](../atomic/readme.md)
* [unsafe类](../base/unsafe/readme.md)

##### 案例
* 32位系统中double 和 long等64位变量的写入时，如果64位变量的写入不在同一个总线事物中，那么该次写入不满足原子性


## 多线程编程工具类
* [BlockingQueue](../queue/readme.md)
* [concurrentHashMap](../hash/concurrentHashMap.md)
* [fork/join](../multiThread/forkjoin/readme.md)
* [LockSupport](../lock/readme.md)
* [ThreadPool](../multiThread/pool/readme.md)
* [CompletableFuture](../multiThread/completablefuture/readme.md)
* [CountDownLatch、CyclicBarrier、Semaphore](util/MultiThreadUtil.md)


# 线程总结
* 线程 
  * 问题1： 线程 vs 进程
  * 问题2：多线程一定比单线程好吗？
* 线程状态
  * 问题1： java线程状态切换方法
  * 问题2： lockSupport vs synchronized线程状态对比
  * 问题3:  waiting 和 blocked 区别
* 线程通信
  * 不同线程：共享内存
    * 问题1: 什么是伪共享
    * 问题2: CAS的ABA如何解决 （volatile的缺陷）
    * 问题3: 堆和栈的区别
  * 线程内栈帧间对象引用传递,线程间数据隔离
    * 问题: threadLocal的原理
* 线程协作
  * 线程同步
    * 问题1: java中锁的分类
    * 问题2: 请对比下 Synchronized 和 ReentrantLock 的异同 ？
    * 问题3: synchronized 锁升级 引出 锁的性能优先级
    * 问题4: CompletableFuture vs fork-join
    * 问题5: future接口的作用
  * 线程通知
    * 问题1: wait-notify vs condition
    * 问题2: CountDownLatch vs wait-notify
  * 闭锁 & 信号量
    * 问题1: CyclicBarrier vs CountDownLatch
    * 问题2: Semaphore vs 读锁
  * 线程安全的集合
    * 问题1: java中同步队列有那些
    * 问题2: HashMap vs concurrentHashMap 
      * 问题3: 同步集合 VS 并发集合 （hashTable vs concurrentHashMap·）

[面试题](面试题.md)
