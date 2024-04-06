package com.dugq.io;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.FutureTask;

/**
 * Created by dugq on 2024/3/6.
 */
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.out.println(new Son(5).getValue());
    }

    public static int hammingDistance(int x, int y) {

        FutureTask task = new FutureTask(()->{
            return x ^ y;
        });
        Thread thread = new Thread(task);
        thread.run();
        int dif = x ^ y;
        int result = 0;
        while(dif>0){
            int temp = dif & 1;
            if(temp > 0){
                result++;
            }
            dif>>=1;
        }
        return result;
    }



    public static class Person{
        public int value;

        public Person(int value) {
            setValue(value);
        }

        public int getValue() {
            value++;
            this.setValue(value);
            System.out.println(value);
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static  class Son extends Person{
        public Son(int value) {
            super(value);
            setValue(getValue()-3);
        }

        public void setValue(int value) {
            super.setValue(value*2);
        }
    }
}
