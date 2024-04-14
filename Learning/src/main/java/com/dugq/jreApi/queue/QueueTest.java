package com.dugq.jreApi.queue;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by dugq on 2023/4/20.
 */
public class QueueTest {
    private static class Node implements Delayed {
        private final Integer level;
        private final Long delay;

        /**
         *
         * @param level 排序权重，越大越靠后
         * @param delay 延时时间，单位秒
         */
        public Node(Integer level, Integer delay) {
            this.delay = delay*1000 + System.currentTimeMillis();
            this.level = level;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long convert = unit.convert(delay-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            return convert;
        }

        @Override
        public int compareTo(Delayed o) {
            if (o instanceof Node){
                Node target = (Node)o;
                return this.level - target.level;
            }
            return -1;
        }

        @Override
        public String toString() {
            return "{" +
                    "level=" + level +
                    ", delay=" + delay +
                    '}';
        }
    }

    // 按照compareTo 方法进行升序排列，权重低的先出队，即使delay更短，只要它权重低，也得等着
    @Test
    public void testDelayQueue() throws InterruptedException {
        DelayQueue<Node> delayQueue = new DelayQueue<>();
        delayQueue.offer(new Node(3,30));
        delayQueue.offer(new Node(2,50));
        System.out.println(delayQueue.take());
        System.out.println(delayQueue.take());

    }

    @Test
    public void testArrayQueue() throws InterruptedException {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(11);
        queue.add(1);
        queue.take();
        queue.offer(1);
        queue.peek();
        queue.poll();
    }

    @Test
    public void testLinkedBlockingQueue() throws InterruptedException {
        LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
        queue.add(1);
        queue.take();
        queue.offer(1);
        queue.peek();
        queue.poll();
    }

    @Test
    public void testConcurrentLinkedBlockingQueue() throws InterruptedException{
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<Integer>();
        queue.add(1);
        queue.offer(1);
        queue.peek();
        queue.poll();
        queue.remove();
    }
    @Test
    public void testLinkedTransferQueue() throws InterruptedException{
        LinkedTransferQueue<Integer> queue = new LinkedTransferQueue<Integer>();
        queue.add(1);
        queue.take();
        queue.offer(1);
        queue.peek();
        queue.poll();
    }

    @Test
    public void testPriorityQueue() throws InterruptedException{
        PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();
        queue.add(1);
        queue.offer(1);
        queue.peek();
        queue.poll();
    }

}
