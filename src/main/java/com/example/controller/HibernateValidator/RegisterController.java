package com.example.controller.HibernateValidator;

import com.example.pojo.User;
import com.example.pojo.annotation.MyAnnotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by dugq on 2017/6/27.
 */
@Controller
@RequestMapping("/user")
public class RegisterController{
    @RequestMapping("toRegister")
    public String toRegister(){
        return "register";
    }
    @RequestMapping("register")
    public String register(@Validated({NAME.class,AGE.class}) User user, ModelMap modelMap){
        modelMap.addAttribute("user",user);
        return "register";
    }

    @RequestMapping("register2")
    public String register2(@Validated(NAME.class) User user, ModelMap modelMap){
        modelMap.addAttribute("user",user);
        return "register";
    }
}
