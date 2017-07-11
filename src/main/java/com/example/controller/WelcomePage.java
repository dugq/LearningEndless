package com.example.controller;

import com.example.pojo.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dugq on 2017/6/22.
 */
@Controller
@RequestMapping("/")
public class WelcomePage {
    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String index(ModelMap model){
        User smallMing = new User(1,null, 1,19);
        userService.insert(smallMing);
        return "index";
    }

    @RequestMapping("ajax")
    @ResponseBody
    public Object message(){
        throw new RuntimeException("ajax");
    }
}
