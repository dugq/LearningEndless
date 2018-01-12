package com.example.controller.login;

import com.example.controller.BaseController;
import com.example.pojo.dto.ResultBean;
import com.example.pojo.entry.User;
import com.example.service.UserService;
import com.example.util.HttpUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dugq on 2017/10/26.
 */
@Controller
@RequestMapping("user")
public class LoginController extends BaseController{
    protected static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userServiceImpl;

    @RequestMapping("login")
    public String login( User user,ServletRequest request){
        if(isUserLogin(request)){
            return "home";
        }
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            Session session = subject.getSession(false);
            String ipAddr = HttpUtils.getIpAddr((HttpServletRequest) request);
            String browser = HttpUtils.getBrowser((HttpServletRequest) request);
            session.setAttribute("shiro_ip",ipAddr);
            session.setAttribute("shiro_browser",browser);
            Map<Object, Object> resources = ThreadContext.getResources();
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

    public boolean isUserLogin(ServletRequest request){
        return false;
    }


    @RequestMapping("login4ajax")
    @ResponseBody
    public ResultBean login4ajax(@RequestBody User user,ServletRequest request){
        ResultBean resultBean = new ResultBean();
        if(isUserLogin(request)){
            resultBean.setCode("0");
            return resultBean;
        }
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
            Subject subject = SecurityUtils.getSubject();
            System.out.println(subject.isAuthenticated());
            subject.login(token);
            String sessionId = subject.getSession().getId().toString();
            resultBean.setCode("0");
            resultBean.addAttribute("token",sessionId);
            System.out.println(sessionId);
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
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存
        }
        return "redirect:/";
    }

    @RequestMapping("test")
    public String test(){
        ServletContext context = getContext();
        User user = userServiceImpl.selectByPrimaryKey(1);
        User user1 = userServiceImpl.selectByPrimaryKey(2);
        user.setUsername("23");
        user1.setUsername("23");
        userServiceImpl.updateByPrimaryKey(user);
        userServiceImpl.test(user1);
        return "test1";
    }

}
