package com.example.controller.HibernateValidator;

import com.example.pojo.User;
import com.example.pojo.annotation.MyAnnotation.*;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

/**
 * Created by dugq on 2017/6/27.
 */
@Controller
@RequestMapping("/user")
public class RegisterController{
    @Autowired
    private UserService userService;

    @RequestMapping("toRegister")
    public String toRegister(){
        return "register";
    }
    @RequestMapping("register")
    public String register(@Validated({NAME.class,AGE.class}) User user, ModelMap modelMap){
        userService.insert(user);
        modelMap.addAttribute("user",user);
        return "register";
    }

    @RequestMapping("register2")
    public String register2( User user, ModelMap modelMap){
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        //验证Bean参数，并返回验证结果信息
        Set<ConstraintViolation<User>> validators = validator.validate(user,NAME.class);
        if(!CollectionUtils.isEmpty(validators)){
            throw new ConstraintViolationException(validators);
        }
        userService.insert(user);
        modelMap.addAttribute("user",user);
        return "register";
    }

    @RequestMapping("/list")
    public String list(ModelMap modelMap){
        List<User> users = userService.selectAll();
        modelMap.addAttribute("list",users);
        return "lsit";
    }
}
