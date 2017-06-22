package com.example.controller;

import com.example.Six;
import com.example.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by dugq on 2017/6/22.
 */
@Controller
@RequestMapping("/")
public class WelcomePage {
    @RequestMapping("/")
    public String index(ModelMap model){
        User smallMing = new User(1,"小明", Six.man,19);

        model.addAttribute("message","hello world!");
        model.addAttribute("person",smallMing);
        return "index";
    }
}
