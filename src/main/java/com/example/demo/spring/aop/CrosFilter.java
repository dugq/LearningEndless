package com.example.demo.spring.aop;

import com.example.demo.spring.util.HttpUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dugq on 2017\11\7 0007.
 */
@WebFilter
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CrosFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //这里填写你允许进行跨域的主机ip
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        //允许的访问方法
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
        //Access-Control-Max-Age 用于 CORS 相关配置的缓存
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "content-type,sessionid,x-requested-with");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        if(!HttpUtils.isOptionsRequest(request)){
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
