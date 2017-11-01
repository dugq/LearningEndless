package com.example.controller.login;

import com.example.pojo.entry.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dugq on 2017/10/26.
 */
@Controller
@RequestMapping("user")
public class LoginController {

    @RequestMapping("login")
    public String login( User user){
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
            SecurityUtils.getSubject().login(token);
            return "home";
        }catch (AccountException e){
            System.out.println("账号密码错误");
            return "redirect:template/login.html";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "redirect:template/login.html";
        }
    }

    @RequestMapping("/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存
        }
        return "redirect:/";
    }

}
