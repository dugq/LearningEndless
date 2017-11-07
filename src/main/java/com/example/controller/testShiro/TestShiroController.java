package com.example.controller.testShiro;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dugq on 2017/11/7.
 */
@RestController
@RequestMapping("/testShiro")
public class TestShiroController {

    @RequestMapping("test1")
    public String test1(){
        return "test1";
    }
    @RequestMapping("test2")
    public String test2(){
        return "test2";
    }
    @RequestMapping("test3")
    public String test3(){
        return "test3";
    }
    @RequestMapping("test4")
    public String test4(){
        return "test4";
    }



}
