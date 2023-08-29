package com.example.demo.spring.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by dugq on 2017/6/27.
 */
@ControllerAdvice
@Slf4j
public class ExceptionAdvice {
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({ConstraintViolationException.class})
    public Object handleServiceException(HttpServletRequest request, ConstraintViolationException e, HttpServletResponse response) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = violation.getMessage();
        if(isAjax(request)){
            ServletOutputStream outputStream = null;
            try {
                outputStream = response.getOutputStream();
                outputStream.write(JSON.toJSONString(new AjaxResult(-1,message)).getBytes("UTF-8"));
                outputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }else{
            request.setAttribute("error",message);
            return "error";
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class})
    public Object handleServiceException(HttpServletRequest request, BindException e, HttpServletResponse response) {
        List<ObjectError> allErrors = e.getAllErrors();
        String message = "";
        for(ObjectError error : allErrors){
            message += error.getDefaultMessage()+"<br/>";
        }
        if(isAjax(request)){
            ServletOutputStream outputStream = null;
            try {
                outputStream = response.getOutputStream();
                outputStream.write(JSON.toJSONString(AjaxResult.failure(message)).getBytes());
                outputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }else{
            request.setAttribute("error",message);
            return "error";
        }
    }

    /**
     * 500 - Internal Server Error
     */
    @ResponseStatus(HttpStatus.CREATED)
    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletRequest request, Exception e, HttpServletResponse response) {
        log.error("exception advice",e);
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
        boolean b = !StringUtils.isEmpty(request.getHeader("X-Requested-With"));
        return true;
    }


    public static class AjaxResult{
        private String message;
        private int code ;
        AjaxResult(String message){
            this.message = message;
        }

        AjaxResult(int code ,String message){
            this.code = code;
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
