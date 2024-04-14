package com.dugq.JVM.monitoring;

import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * Created by dugq on 2024/4/7.
 */
public class RuntimeMonitor {

    @Test
    public void testRuntime(){
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        System.out.println("freeMemory"+freeMemory);
        System.out.println("maxMemory"+maxMemory);
        System.out.println("totalMemory"+totalMemory);
        testJMX();
    }

    @Test
    public void testJMX(){
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long used = heapMemoryUsage.getUsed();
        long committed = heapMemoryUsage.getCommitted();
        long max = heapMemoryUsage.getMax();
        long init = heapMemoryUsage.getInit();
        System.out.println("used"+used);
        System.out.println("committed"+committed);
        System.out.println("max"+max);
        System.out.println("init"+init);
    }


}
