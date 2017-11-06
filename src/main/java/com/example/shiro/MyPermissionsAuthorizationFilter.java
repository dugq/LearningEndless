package com.example.shiro;

import com.alibaba.fastjson.JSON;
import com.example.pojo.entry.User;
import com.example.pojo.dto.ResultBean;
import com.example.service.OperationsService;
import com.example.service.UserService;
import com.example.util.HttpUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by dugq on 2017/11/1.
 */
@WebFilter
@Order(Integer.MAX_VALUE)
public class MyPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {
    @Autowired
    private OperationsService operationsService;

    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return super.onPreHandle(request, response, mappedValue);
    }

    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        if(HttpUtils.isOptionsRequest(request)){
            return true;
        }
        Subject subject = SecurityUtils.getSubject();
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        Object principal = subject.getPrincipal();
        User user = null;
        if(principal instanceof User){
             user = (User) principal;
        }else{
            return false;
        }
        List<String> strings = operationsService.selectPermsListByUser(user.getUid());
        String requestURI = httpServletRequest.getRequestURI();
        return strings.contains(requestURI);
    }

//    @Override
//    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
//        if(HttpUtils.isOptionsRequest(request)){
//            return true;
//        }
//        Subject subject = SecurityUtils.getSubject();
//        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
//        String requestURI = httpServletRequest.getRequestURI();
//        boolean permitted = subject.isPermitted(requestURI);
//        return permitted;
//    }


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

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        Subject subject = this.getSubject(request, response);
        if (subject.getPrincipal() == null) {
            this.saveRequestAndRedirectToLogin(request, response);
        } else {
            String unauthorizedUrl = this.getUnauthorizedUrl();
            if (StringUtils.hasText(unauthorizedUrl)) {
                WebUtils.issueRedirect(request, response, unauthorizedUrl);
            } else {
               HttpServletResponse httpServletResponse = (HttpServletResponse)response;
                if(HttpUtils.isAjax((HttpServletRequest) request)){
                    HttpUtils.writeJson2Response(httpServletResponse,"-10","noPermission");
                }else{
                   httpServletResponse.sendRedirect("/template/noPermission.html");
                }
            }
        }

        return false;
    }


}
