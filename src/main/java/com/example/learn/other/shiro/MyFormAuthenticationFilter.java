package com.example.learn.other.shiro;

import com.example.demo.spring.util.HttpUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by dugq on 2017/10/31.
 */
public class MyFormAuthenticationFilter extends FormAuthenticationFilter {

    //最后执行的方法，放在finally中
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return super.onAccessDenied(request, response);
    }

    /*判断是否登陆*/
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        boolean authenticated = subject.isAuthenticated();
       if (!isLoginRequest(request, response) && isPermissive(mappedValue) && !Objects.isNull(subject.getPrincipals())){
           return true;
       }
       return authenticated;

    }



    /*当拦截返回false的时候执行*/
    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        if(HttpUtils.isAjax((HttpServletRequest) request)){
            HttpUtils.writeJson2Response((HttpServletResponse) response,"登陆过期,请重新登录","-5");
            return;
        }
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        httpServletResponse.sendRedirect("/template/login.html");
    }



}
