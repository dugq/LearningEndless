package com.example.BasicComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by dugq on 2017/7/10.
 */
public class BasicComponent {
   protected static final Logger logger = LoggerFactory.getLogger(BasicComponent.class);

   public void getRequest(){
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

      HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();

      HttpServletResponse response1 = ((ServletWebRequest)RequestContextHolder.getRequestAttributes()).getResponse();

      ServletContext context = ContextLoader.getCurrentWebApplicationContext().getServletContext();
   }

   public static void main(String[] args) {
      double v = Math.random();
      String tel = String.valueOf(v).substring(2,13);
      System.out.print(tel);
   }
}
