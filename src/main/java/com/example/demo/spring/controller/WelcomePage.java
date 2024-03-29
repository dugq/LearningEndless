package com.example.demo.spring.controller;

import com.example.demo.spring.pojo.User;
import com.example.demo.spring.pojo.statics.StaticVar;
import com.example.demo.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

/**
 * Created by dugq on 2017/6/22.
 */
@Controller
@RequestMapping("/")
public class WelcomePage {
    @Autowired
    private StaticVar staticVar;
    @Autowired
    private UserService userService;
    @Autowired
    private Validator validator;

    @RequestMapping("/")
    public String index( ModelMap model){
        User smallMing = new User(1,null, 1,19);
        Set<ConstraintViolation<User>> validate = validator.validate(smallMing,new Class[]{});
        if(!CollectionUtils.isEmpty(validate)){
            throw  new ConstraintViolationException(validate);
        }
//        userService.insert(smallMing);
        return "index";
    }

    @RequestMapping("ajax")
    @ResponseBody
    public Object message(){
        throw new RuntimeException("ajax");
    }


    @RequestMapping("test")
    @ResponseBody
    public String test(){
        System.out.println(StaticVar.test);
        System.out.println(StaticVar.url);
        System.out.println(StaticVar.test1);
        String url = StaticVar.url;
        return url;
    }

    @RequestMapping("/testResponseInt")
    public void testResponse(HttpServletResponse response) throws IOException {
        response.getOutputStream().write("123".getBytes());
        response.getOutputStream().close();
    }
}
