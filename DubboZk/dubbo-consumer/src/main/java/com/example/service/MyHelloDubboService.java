package com.example.service;

/**
 * Created by dugq on 2023/9/1.
 */
public class MyHelloDubboService implements HelloDubboService{

    @Override
    public String say(String msg) {
        return "hello!"+msg+" i am consumer!";
    }
}
