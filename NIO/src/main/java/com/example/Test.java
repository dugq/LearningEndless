package com.example;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dugq on 2023/5/16.
 */
public class Test {

    public String oomTest(){
        List<Test> test = new LinkedList<>();
        boolean falg = true;
        while (falg){
            test.add(new Test());
        }
        return "";
    }

    public String testStaticObj(){
        List<String> test = new LinkedList<>();
        boolean flag = true;
        long i =0;
        while (flag){
            test.add(String.valueOf(i++).intern());
            if (i%1000==0){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return "";
    }
}
