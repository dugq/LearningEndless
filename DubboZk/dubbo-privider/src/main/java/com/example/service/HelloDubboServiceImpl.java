package com.example.service;

import org.apache.dubbo.config.annotation.DubboService;

/**
 * Created by dugq on 2023/9/1.
 */
@DubboService
public class HelloDubboServiceImpl implements HelloDubboService{

    @Override
    public String say(String msg) {
        return "hello !"+ msg + " come from dubbo provider";
    }
}
