package com.example.controller;

import com.example.Six;
import com.example.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dugq on 2017/6/22.
 */
@Controller
@RequestMapping("/")
public class WelcomePage {
    @RequestMapping("/")
    public String index(ModelMap model){
        User smallMing = new User(1,"小明", "男",19);
        model.addAttribute("message","<b>{1}hello  world!{0}</b>");
        model.addAttribute("person",smallMing);
        if(true){
         throw new RuntimeException("fdsaf");
        }
        return "index";
    }

    @RequestMapping("ajax")
    @ResponseBody
    public Object message(){
        throw new RuntimeException("ajax");
    }
}
