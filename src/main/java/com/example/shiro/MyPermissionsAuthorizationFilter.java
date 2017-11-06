package com.example.shiro;

import com.alibaba.fastjson.JSON;
import com.example.pojo.dto.ResultBean;
import com.example.service.OperationsService;
import com.example.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
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
        Subject subject = SecurityUtils.getSubject();
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String str = httpServletRequest.getRequestURI();
        List<String> strings = operationsService.selectPermsListByUrl(str);
        String[] perms = strings.toArray(new String[]{});
        boolean isPermitted = true;
        if (perms != null && perms.length > 0) {
            if (perms.length == 1) {
                if (!subject.isPermitted(perms[0])) {
                    isPermitted = false;
                }
            } else {
                if (!subject.isPermittedAll(perms)) {
                    isPermitted = false;
                }
            }
        }


        return isPermitted;
    }

    /*当拦截返回false的时候执行*/
    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        if(isAjax((HttpServletRequest) request)){
            writeJson2Response((HttpServletResponse) response,"登陆过期","-5");
            return;
        }
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        httpServletResponse.sendRedirect("/template/login.html");
    }

    public boolean isAjax(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(header) ? true:false;
    }

    private void writeJson2Response(HttpServletResponse response,
                                    String message, String code) {
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            response.setHeader("Content-Type",
                    "application/json;charset=UTF-8");
            outputStream.write(JSON.toJSONString(new ResultBean(code, message))
                    .getBytes("UTF-8"));
            outputStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
