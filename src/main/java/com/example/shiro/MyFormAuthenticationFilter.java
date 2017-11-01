package com.example.shiro;

import com.alibaba.fastjson.JSON;
import com.example.pojo.dto.ResultBean;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletOutputStream;
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
        if(request instanceof HttpServletRequest){
            HttpServletRequest res = (HttpServletRequest) request;
            String method = res.getMethod();
            if(method.equals("OPTIONS")){
                return true;
            }
        }
        return super.onAccessDenied(request, response);
    }

    /*判断是否登陆*/
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        boolean authenticated = subject.isAuthenticated();
       if (!isLoginRequest(request, response) && isPermissive(mappedValue)){
           return true;
       }
       return authenticated;

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
