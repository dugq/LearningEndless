package com.example.learn.JVM.vmRunSystem.compile;

/**
 * Created by dugq on 2023/5/15.
 */
public class testJavaC {

    public int add(int A){
        int a = A;
        int b = 20;
        int c = (a+b)*100;
        int d = c;
        int e = d;
        int f = e;
        int h = f;
        Integer m;
        return add(h);
    }

    public Integer add2(){
        Integer a = 1;
        Integer b = a;
        Integer c = a+b;
        return c;
    }

}
