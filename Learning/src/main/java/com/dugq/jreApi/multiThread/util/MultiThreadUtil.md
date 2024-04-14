# CountDownLatch
[CountDownLatchTest](CountDownLatchTest.java)
* 内部自定义一个AQS的实现类
* AQS初始化state为目标任务数
* AQS重写了加锁和解锁方法，解锁是将state - 1，而获取锁是通过判断state是否为0。因此countDownLatch是不能重复使用的
* countDown方法调用的是AQS的解锁方法，await方法调用加锁方法.千万别用错了，写成了wait
* 当锁状态不为0时，当前线程会死等
* 主线程阻塞，自线程是没有阻塞的

# CyclicBarrier
[CyclicBarrierTest](CyclicBarrierTest.java)
* 内部使用 ReentrantLock 和 Condition实现
* await 响应中断事件，当一个线程被中断时，其他所有线程都会中断
* 当所有线程都到达屏障时，触发command，command如果发生异常，所有线程中断

~~~java 
 public class CyclicBarrier{
    
    private int dowait(boolean timed, long nanos) throws InterruptedException, BrokenBarrierException, TimeoutException {
        final ReentrantLock lock = this.lock;
        //通过锁机制来保证count 、 generation 的线程安全
        lock.lock();
        try {
            // 通过Generation.broken 让相关线程全部响应中断。当某一个线程在await时发生中断或者超时退出，那么其他线程不会死锁，而是抛出BrokenBarrierException异常
            final Generation g = generation;
            if (g.broken)
                throw new BrokenBarrierException();
            // 每个线程在await时都会响应中断
            if (Thread.interrupted()) {
                breakBarrier();
                throw new InterruptedException();
            }
            int index = --count;
            // 当count递减到0 时，执行barrierCommand，然后调用nextGeneration唤起所有等待挂起
            if (index == 0) {  
                boolean ranAction = false;
                try {
                    final Runnable command = barrierCommand;
                    if (command != null)
                        command.run();
                    ranAction = true;
                    nextGeneration();
                    return 0;
                } finally {
                // 如果 barrierCommand 发生异常，那所有线程都中断并抛出BrokenBarrierException    
                    if (!ranAction)
                        breakBarrier();
                }
            }
            for (;;) {
                try {
                    // 如果没有超时设置就使用 condition死等。使用condition进行阻塞线程
                    if (!timed)
                        trip.await();
                    else if (nanos > 0L)
                        nanos = trip.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    if (g == generation && ! g.broken) {
                        breakBarrier();
                        throw ie;
                    } else {
                        // 如果线程挂起，也会响应interrupt
                        Thread.currentThread().interrupt();
                    }
                }
                if (g.broken)
                    throw new BrokenBarrierException();
                if (g != generation)
                    return index;
                if (timed && nanos <= 0L) {
                    breakBarrier();
                    throw new TimeoutException();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
~~~

# Semaphore
[Semaphore](../SemaphoreTest.java)
* 自定义AQS实现，共享锁


### 这三个工具类的区别
* CountDownLatch 是主线程等待所有子线程完成后再继续执行。其实可以使用ForkJoin代替，但是ForkJoin更关注拆分递归任务
* CyclicBarrier 是没有主次之分的，它是要求所有线程到达某个点后，再一起往后冲。它会使得所有线程发生阻塞
* Semaphore 是控制线程必要太快了，同时只能有多少个线程通过
