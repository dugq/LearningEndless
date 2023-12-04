package com.dugq.jreApi.multiThread.util;

import com.dugq.ThreadUtil;
import org.junit.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dugq on 2023/11/29.
 */
public class ThreadLocalTest {
    ThreadLocal<Long> time = new ThreadLocal<>();

    @Test
    public void simpleTest() throws InterruptedException {
        Object lock1 = new Object();
        Object lock2 = new Object();
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(()->{
            time.set(1L);
            ThreadUtil.printThreadInfo("set time = 1 ");
            ThreadUtil.threadWait(lock1);
            ThreadUtil.threadNotify(lock2);
            ThreadUtil.printThreadInfo("get time = "+time.get());
            countDownLatch.countDown();
        },"threadlocal=thread-1").start();
        ThreadUtil.sleep(1);
        new Thread(()->{
            time.set(2L);
            ThreadUtil.printThreadInfo("set time = 2 ");
            ThreadUtil.threadNotify(lock1);
            ThreadUtil.threadWait(lock2);
            ThreadUtil.printThreadInfo("get time = "+time.get());
            countDownLatch.countDown();
        },"threadlocal=thread-2").start();
        countDownLatch.await();
    }

    @Test
    public void debugSetAndGet(){
        time.set(1L);
        Long val = time.get();
        System.out.println("val="+val);
    }

    @Test
    public void testThreadLocalWeakReference() throws NoSuchFieldException, IllegalAccessException {
        MyThreadLocal<String> TestString = new MyThreadLocal<>("gc-threadLocal");
        MyThreadLocal<String> TestString2 = new MyThreadLocal<>("not-gc-threadLocal");
        TestString.set("testWeak");
        TestString2.set("testWeak2");
        TestString = null;
        System.gc();
        printCurrentThreadAllStringValueInThreadLocal();
    }

    private static void printCurrentThreadAllStringValueInThreadLocal() throws NoSuchFieldException, IllegalAccessException {
        Thread thread = Thread.currentThread();
        Field threadLocalsFilled = Thread.class.getDeclaredField("threadLocals");
        threadLocalsFilled.setAccessible(true);
        Object threadLocals = threadLocalsFilled.get(thread);
        Field entryFiled = threadLocals.getClass().getDeclaredField("table");
        entryFiled.setAccessible(true);
        Object[] entryArray = (Object[])entryFiled.get(threadLocals);
        for (Object entry : entryArray){
            if (Objects.isNull(entry)){
                continue;
            }
            StringBuilder sb = new StringBuilder();
            Field valueFiled = entry.getClass().getDeclaredField("value");
            valueFiled.setAccessible(true);
            Object value = valueFiled.get(entry);
            if (value instanceof String){
                WeakReference<?> weakReference = (WeakReference<?>) entry;
                Object reference = weakReference.get();
                if (Objects.nonNull(reference)){
                    if (reference instanceof MyThreadLocal){
                        MyThreadLocal<?> threadLocal = (MyThreadLocal<?>) reference;
                        sb.append("name = ").append(threadLocal.getName());
                    }else{
                        sb.append("name not know ");
                    }
                }else{
                    sb.append("name = null ");
                }
                sb.append(" value = ").append(value);
            }
            System.out.println(sb);
        }
    }

    static class MyThreadLocal<T> extends ThreadLocal<T>{
        private String name;
        public MyThreadLocal(String name){
            this.name = name;
        }

        public String getName(){
            return this.name;
        }
    }

    @Test
    public void testWeakReference(){
        Reference<Object> ref;

        Object weakObj = new String("weak reference");
        Object phantomObj = new String("phantom reference");
        ReferenceQueue<Object> weakQueue = new ReferenceQueue<Object>();
        ReferenceQueue<Object> phantomQueue = new ReferenceQueue<Object>();
        WeakReference<Object> weakReference = new WeakReference<Object>(weakObj, weakQueue);
        PhantomReference<Object> phantomReference = new PhantomReference<Object>(phantomObj, phantomQueue);
        String myWeakKey = new String("myWeakKey");
        String myEntry = new String("myEntry");
        Entry entry = new Entry(myWeakKey, myEntry);
        weakObj = null;
        phantomObj = null;
        myWeakKey = null;
        myEntry = null;
        System.out.println("Weak Reference: "+weakReference.get());
        System.out.println("Phantom Reference: "+phantomReference.get());//null
        System.gc();//弱引用、虚引用被回收，同时加入到相关ReferenceQueue
        System.out.println("Weak Reference: "+weakReference.get());//null
        System.out.println("Phantom Reference: "+phantomReference.get());//null
        System.out.println("weakKey "+entry.get());
        System.out.println("weakValue "+entry.value);

        if(!phantomReference.isEnqueued()) {
            System.out.println("Request finalization.");
            System.runFinalization();
        }
        System.out.println("weak is queued: "+weakReference.isEnqueued());//true
        System.out.println("Phantom Queued: " + phantomReference.isEnqueued());//true

        try {
            ref = (Reference<Object>) weakQueue.remove();
            System.out.println("Weak Reference: " + ref.get());//null
            ref = (Reference<Object>) phantomQueue.remove();
            System.out.println("Phantom Reference: " + ref.get());//null
            ref.clear();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Entry extends WeakReference<String> {
        /** The value associated with this ThreadLocal. */
        Object value;

        Entry(String k, Object v) {
            super(k);
            value = v;
        }
    }

}
