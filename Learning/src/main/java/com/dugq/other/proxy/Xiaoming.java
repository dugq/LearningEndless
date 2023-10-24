package com.dugq.other.proxy;

/**
 * Created by dugq on 2018/4/26.
 */
public class Xiaoming implements Person {
    @Override
    public void say() {
        System.out.println("my name is xiaoimng");
    }

    @Override
    public void eat() {
        System.out.println("i am eating");
    }
}
