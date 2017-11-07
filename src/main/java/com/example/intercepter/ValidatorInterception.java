package com.example.intercepter;

import com.example.pojo.annotation.MyValidator;
import com.example.util.HttpUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2017/7/11.
 */
public class ValidatorInterception implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if(HttpUtils.isOptionsRequest(httpServletRequest)){
            return true;
        }
        HandlerMethod method = (HandlerMethod)o;
        Method method1 = method.getMethod();
        List<Object> objs = new ArrayList<Object>();
        MethodParameter[] methodParameters = method.getMethodParameters();
        for(int i = 0 ; i < methodParameters.length; i++){
            MyValidator parameterAnnotation = methodParameters[i].getParameterAnnotation(MyValidator.class);
            String parameter = httpServletRequest.getParameter(methodParameters[i].getParameterName());
            if(!Objects.isNull(parameterAnnotation)){
            }
        }

        Parameter[] parameters = method1.getParameters();
        for(int i = 0 ; i < parameters.length; i++){
            MyValidator annotation = parameters[i].getAnnotation(MyValidator.class);
            if(!Objects.isNull(annotation)){
               /* objs.add()*/
            }

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
