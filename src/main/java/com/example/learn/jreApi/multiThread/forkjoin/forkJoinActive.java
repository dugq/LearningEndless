package com.example.learn.jreApi.multiThread.forkjoin;

import com.alibaba.fastjson.JSON;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by dugq on 2023/10/19.
 * 模拟：
 *  对100万数据行进行求和，求平均
 * 策略：
 *  mapreduce
 */
public class forkJoinActive {

    public static class User{
        public User(Integer age) {
            this.age = age;
        }

        private Integer age;
    }

    public static List<User> getFromDb(int start, int size) {
        int sum = 0;
        for (int i=0 ;i < 10000; i++){
            sum += sum * i;
        }
        ArrayList<User> users = new ArrayList<>();
        for (int index =0; index < size; index++){
            users.add(new User(index+start));
        }
        return users;
    }

    private static ExecutorService executorService = new ThreadPoolExecutor(10,100,10000, TimeUnit.SECONDS,new ArrayBlockingQueue<>(100000));

    @Test
    public void test() throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        MyTask1 task = new MyTask1(0,1000000);
        executorService.execute(task);
        Integer sum = task.getResult().get();
        System.out.println("sum="+sum+" time="+(System.currentTimeMillis()-startTime)+" count="+MyTask1.count.get());
        //10线程 sum=1783293664 time=11265
        //4线程  sum=1783293664 time=28362

    }

    static class MyTask1 implements Runnable {
        private int start;
        private int end;

        volatile Future<Integer> result1;
        volatile Future<Integer> result2;

        private volatile Integer result;

        private static AtomicInteger count = new AtomicInteger();

        public MyTask1(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public void run(){
            count.addAndGet(1);
            if (end-start>100){
                int mid = start + (end - start) / 2;
                mid = mid/100*100;
                MyTask1 task1 = new MyTask1(start, mid);
                MyTask1 task2 = new MyTask1(mid, end);
                executorService.execute(task1);
                executorService.execute(task2);
                result1 = task1.getResult();
                result2 = task2.getResult();
            }else{
                List<User> users = getFromDb(start, end - start);
                result = users.stream().map(user -> user.age).reduce(0, Integer::sum);
            }
        }

        private Future<Integer> getResult() {
            if (Objects.nonNull(result)){
                return new Future<Integer>() {
                    @Override
                    public boolean cancel(boolean mayInterruptIfRunning) {
                        return false;
                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }

                    @Override
                    public boolean isDone() {
                        return true;
                    }

                    @Override
                    public Integer get() throws InterruptedException, ExecutionException {
                        return result;
                    }

                    @Override
                    public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                        return result;
                    }
                };
            }else if (Objects.nonNull(result1) && Objects.nonNull(result2)){
                return new Future<Integer>() {
                    @Override
                    public boolean cancel(boolean mayInterruptIfRunning) {
                        return result1.cancel(mayInterruptIfRunning) && result2.cancel(mayInterruptIfRunning);
                    }

                    @Override
                    public boolean isCancelled() {
                        return result1.isCancelled() || result2.isCancelled();
                    }

                    @Override
                    public boolean isDone() {
                        return result1.isDone() && result2.isDone();
                    }

                    @Override
                    public Integer get() throws InterruptedException, ExecutionException {
                        return result1.get() + result2.get();
                    }

                    @Override
                    public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                        return result1.get(timeout,unit) + result2.get(timeout,unit);
                    }
                };
            }else{
                return new Future<Integer>() {
                    @Override
                    public boolean cancel(boolean mayInterruptIfRunning) {
                        return false;
                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }

                    @Override
                    public boolean isDone() {
                        return false;
                    }

                    @Override
                    public Integer get() throws InterruptedException, ExecutionException {
                        while (true){
                            if (Objects.isNull(result) && (Objects.isNull(result1) || Objects.isNull(result2))){
                                continue;
                            }
                            if (Objects.nonNull(result)){
                                return result;
                            }
                            return result1.get() + result2.get();
                        }
                    }

                    @Override
                    public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                        return get();
                    }
                };
            }
        }

    }

    /**
     * 当 任务中没有阻塞，只有计算时：
     *  线程数 = 核心数*2 时最佳
     *
     * 当 任务中存在阻塞，造成线程状态切换时：
     * 在一定返回内，线程数越多，任务越快
     */
    @Test
    public void testForkJoin() throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool(7);
        ForkJoinTask<Integer> result = forkJoinPool.submit(new MyTask(0, 1000000));
        Integer sum = result.get();
        System.out.println("sum="+sum+" time="+(System.currentTimeMillis()-startTime)+"count="+MyTask.count.get());
        //10线程 sum=1783293664 time=150776
        //4线程 sum=1783293664 time=46723
    }

    private static class MyTask extends RecursiveTask<Integer> {
        private static final long serialVersionUID = -8577433021948400913L;

        private Integer start;

        private Integer end;

        private static Map<String,Integer> hashMap = new ConcurrentHashMap<>();

        public static AtomicInteger count = new AtomicInteger();

        public MyTask(Integer start, Integer end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            count.addAndGet(1);
            if (end-start>100){
                int mid = start + (end - start) / 2;
                mid = mid/100*100;
                MyTask task1 = new MyTask(start, mid);
                MyTask task2 = new MyTask(mid, end);
                task1.fork();
                task2.fork();
                Integer join = task2.join();
                return join+task1.join();
            }
            List<User> users = getFromDb(start, end - start);
//            Integer value = hashMap.get(Thread.currentThread().getName());
//            if (Objects.isNull(value)){
//                hashMap.put(Thread.currentThread().getName(),1);
//            }else{
//                hashMap.put(Thread.currentThread().getName(),value+1);
//            }
            return users.stream().map(user -> user.age).reduce(0, Integer::sum);
        }

        public static void print(){
            System.out.println(JSON.toJSONString(hashMap));
            Integer sum = hashMap.values().stream().reduce(0, Integer::sum);
            System.out.println("sum = "+sum);
        }
    }


}
