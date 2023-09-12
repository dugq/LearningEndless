package com.example.service;

import org.apache.dubbo.common.constants.ClusterRules;
import org.apache.dubbo.common.constants.LoadbalanceRules;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dugq on 2023/9/1.
 */
@RestController
public class HelloCtrl {

    @DubboReference(loadbalance= LoadbalanceRules.RANDOM,cluster= ClusterRules.FAIL_FAST,check = false,mock = "com.example.service.MyHelloDubboService")
    private HelloDubboService helloDubboService;

    @GetMapping("/hello")
    public String say(String msg){
        return helloDubboService.say(msg);
    }

}
