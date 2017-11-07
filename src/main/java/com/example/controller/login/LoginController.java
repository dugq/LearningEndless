package com.example.controller.login;

import com.example.pojo.dto.ResultBean;
import com.example.pojo.entry.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by dugq on 2017/10/26.
 */
@Controller
@RequestMapping("user")
public class LoginController {

    @RequestMapping("login")
    public String login(User user, HttpServletRequest request){
        try {
            request.getSession().setAttribute("user",user);
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
    @RequestMapping("login4ajax")
    @ResponseBody
    public ResultBean login4ajax(@RequestBody User user){
        ResultBean resultBean = new ResultBean();
        try {
            throw new RuntimeException("测试");
        }catch (AccountException e){
            resultBean.setCode("-1");
            resultBean.setMessage("账号密码错误");
        }
        catch (Exception e) {
            resultBean.setCode("-1");
            resultBean.setMessage("账号密码错误");
        }finally {
            return resultBean;
        }
    }
    @RequestMapping("/logout")
    public String logout( HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return "redirect:/";
    }

}
