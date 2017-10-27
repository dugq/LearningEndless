package com.example.controller;

import com.example.pojo.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Set;

/**
 * Created by dugq on 2017/6/22.
 */
@Controller
@RequestMapping("/")
public class WelcomePage {
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
}
