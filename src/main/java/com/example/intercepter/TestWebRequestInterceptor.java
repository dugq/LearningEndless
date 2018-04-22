package com.example.intercepter;

import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

/**
 * Created by dugq on 2018/4/19.
 */
public class TestWebRequestInterceptor implements WebRequestInterceptor {
    @Override
    public void preHandle(WebRequest request) throws Exception {
        System.out.println("---------------");
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {

    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {

    }
}
