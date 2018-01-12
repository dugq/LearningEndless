package com.example.BasicComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Created by dugq on 2017/7/10.
 */
public class BasicComponent {
   protected static final Logger logger = LoggerFactory.getLogger(BasicComponent.class);
  @Autowired
   protected WebApplicationContext webApplicationContext;


   protected HttpServletRequest getRequest(){
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();



      return request;
   }

   protected HttpSession getSeesion(){
      return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
   }

   protected  HttpServletResponse getRespose(){
         HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
         /*HttpServletResponse response1 = ((ServletWebRequest)RequestContextHolder.getRequestAttributes()).getResponse();*/
         return  response;
      }

   protected ServletContext   getContext(){
//      WebApplicationContext currentWebApplicationContext = ContextLoader.getCurrentWebApplicationContext();
      return  webApplicationContext.getServletContext();
   }


}

