package com.example.demo.spring.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Predicate;

/**
 *工具类实现跨域
 * 相比于 filter的方式，通过工具类的方式可以控制部分接口的跨域请求，不会导致批量问题
 */
public class CrossDomainUtils {

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

	private CrossDomainUtils(){}

    /**
     * 跨域设置,只允许兑吧范围内跨域访问
     * @param request
     * @param response
     */
    public static void crossDomain(HttpServletRequest request, HttpServletResponse response, Predicate predicate){
        //设置允许跨域
        String domain = request.getHeader("Origin");
        if(domain != null && predicate.test(request)) {
            // 设置允许跨域
            response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN,  addScheme(domain, request));
            response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, "POST,GET");
            response.setHeader(ACCESS_CONTROL_MAX_AGE, "3600");
            response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, "x-requested-with");
            response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }
    }

    public static String addScheme(String url, HttpServletRequest request){
        url = StringUtils.trimToEmpty(url);
        if(request!=null) {
            String requestScheme = request.getScheme();
            if (url.startsWith("//")) {
                url = requestScheme + ":" + url;
            }
        }
        return url;
    }
}

