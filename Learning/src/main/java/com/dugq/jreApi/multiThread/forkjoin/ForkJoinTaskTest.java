package com.dugq.jreApi.multiThread.forkjoin;

import com.example.util.ThreadUtil;
import org.apache.shiro.util.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ForkJoinTaskTest {


    @Test
    public void forkJoinTaskTest() throws ExecutionException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        List<Integer> list = generate();
        RecursiveTask<Integer> recursiveTask = new TestTask(list);
        ForkJoinPool forkJoinPool = new ForkJoinPool(1);
        ForkJoinTask<Integer> submit = forkJoinPool.submit(recursiveTask);
        Field workQueues = forkJoinPool.getClass().getDeclaredField("workQueues");
        workQueues.setAccessible(true);
        ThreadUtil.sleep(1);
        Object o = workQueues.get(forkJoinPool);
        if (o.getClass().isArray()){
            Object[] ob = (Object[])o;
            System.out.println("length="+ob.length);

        }
        // 不对劲 感觉不是初始化了的线程数
        int activeThreadCount = forkJoinPool.getActiveThreadCount();
        int runningThreadCount = forkJoinPool.getRunningThreadCount();
        //这个才是总线程数
        int poolSize = forkJoinPool.getPoolSize();
        forkJoinPool.getRunningThreadCount();
        System.out.println("running thread count = "+runningThreadCount);
        System.out.println("total thread count = "+poolSize);
        System.out.println("thread count = "+activeThreadCount + "\nsubmit1 = "+submit.join() );
    }

    @Test
    public void test(){
        int x = Integer.MAX_VALUE;
        System.out.println((x)& 127);
        System.out.println((x + 1) & 127);
        System.out.println((x + 2) & 127);
        System.out.println((x + 3) & 127);
        System.out.println((x + 4) & 127);
    }

    private class TestTask extends RecursiveTask<Integer>{
        private static final long serialVersionUID = 1795537951586724879L;
        private final List<Integer> list;

        private static final int threshold = 10;
        public TestTask(List<Integer> list){
            this.list = list;
        }

        @Override
        protected Integer compute() {

            if (CollectionUtils.isEmpty(list)){
                return 0;
            }
            if (list.size()>threshold){
                TestTask testTask1 = new TestTask(list.subList(0, list.size() / 2));
                TestTask testTask2 = new TestTask(list.subList(list.size() / 2 ,list.size()));
                testTask1.setForkJoinTaskTag((short)1);
                testTask1.fork();
                testTask2.fork();
                testTask2.setForkJoinTaskTag((short)1);
                // API 建议 innerMost-first
                Integer join = testTask1.join();
                return join + testTask2.join();
            }
            return count(list);
        }

    }

    private int count(List<Integer> list) {
        int count = 0;
        for (Integer i : list) {
            count+=i;
        }
        System.out.println("thread name = "+Thread.currentThread().getName());
        return count;
    }



    public List<Integer> generate(){
        ArrayList<Integer> list = new ArrayList<>(10000000);
        for (int i = 0 ; i<100; i++){
            list.add(i);
        }
        return list;
    }

    @Test
    public void forkJoinPoolTest(){
        ForkJoinPool.commonPool();
    }

}
