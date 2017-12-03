package com.example.controller.testShiro;

import com.example.pojo.dto.ResultBean;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dugq on 2017/11/7.
 */
@RestController
@RequestMapping("/testShiro")
public class TestShiroController {

    @RequestMapping("test1")
    public ResultBean test1(){

        Subject subject = SecurityUtils.getSubject();
        return new ResultBean("0","test1");
    }
    @RequestMapping("test2")
    public ResultBean test2(){
        return new ResultBean("0","test2");
    }
    @RequestMapping("test3")
    public ResultBean test3(){
        return new ResultBean("0","test3");
    }
    @RequestMapping("test4")
    public ResultBean test4(){
        return new ResultBean("0","test4");
    }



}
