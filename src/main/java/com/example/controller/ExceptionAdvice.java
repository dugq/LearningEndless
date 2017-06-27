package com.example.controller;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by dugq on 2017/6/27.
 */
@ControllerAdvice
public class ExceptionAdvice {
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleServiceException(HttpServletRequest request, ConstraintViolationException e, HttpServletResponse response) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        System.out.print("comming--------------------------------------------------");
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = violation.getMessage();
        if(isAjax(request)){
            ServletOutputStream outputStream = null;
            try {
                outputStream = response.getOutputStream();
                outputStream.write(JSON.toJSONString(AjaxResult.failure(e.getMessage())).getBytes());
                outputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }else{
            request.setAttribute("error",e.getMessage());
            return "error";
        }
    }
    /**
     * 500 - Internal Server Error
     */
    @ResponseStatus(HttpStatus.CREATED)
    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletRequest request, Exception e, HttpServletResponse response) {
        System.out.print(e.getClass());
        System.out.print("500-------------------------------------");
        if(isAjax(request)){
            ServletOutputStream outputStream = null;
            try {
                outputStream = response.getOutputStream();
                outputStream.write(JSON.toJSONString(AjaxResult.failure(e.getMessage())).getBytes());
                outputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }else{
            request.setAttribute("error",e.getMessage());
            return "error";
        }
    }

    public boolean isAjax(HttpServletRequest request){
        return !StringUtils.isEmpty(request.getHeader("X-Requested-With"));
    }


    public static class AjaxResult{
        private String message;
        private int code ;
        AjaxResult(String message){
            this.message = message;
        }



       public static AjaxResult failure(String message){
           AjaxResult ajaxResult = new AjaxResult(message);
           ajaxResult.setCode(10000);
           return ajaxResult;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
