package com.example.learn.jreApi.multiThread;

import org.junit.Test;

/**
 * Created by dugq on 2023/5/31.
 */
public class VolatileTest {
    private  volatile boolean flag;
    private  volatile int configA;

    // volatile 修饰的变量flag在线程1中的修改在线程2中可见
    @Test
    public void testVarVisibility() {
        new Thread(this::ThreadA).start();
        new Thread(this::ThreadB).start();
    }

    private  void Thread1(){
        flag = true;
    }

    private  void Thread2(){
        for (int i =0 ;i < 1000; i++){
            if (flag){
                System.out.println("yes");
            }
        }
    }


    // volatile 修饰的变量flag和config 在线程A中的修改在线程B中可见
    @Test
    public void testMemConsistency() {
        new Thread(this::ThreadA).start();
        new Thread(this::ThreadB).start();
    }

    //由于变量configA和flag都用volatile修饰，所以线程A中configA 和 flag 的赋值不会重排序，这样线程B中一定能获取正确的configA
    private  void ThreadA(){
        configA = 5;
        flag = true;
    }

    private  void ThreadB(){
        for (int i =0; i<1000; i++) {
            if (flag){
                System.out.println(configA);
            }
        }
    }


}
