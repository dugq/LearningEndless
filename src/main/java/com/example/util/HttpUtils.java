package com.example.util;

import com.alibaba.fastjson.JSON;
import com.example.pojo.dto.ResultBean;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dugq on 2017\11\6 0006.
 */
public class HttpUtils {
    public static boolean  isAjax(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(header) ? true:false;
    }


    public static void writeJson2Response(HttpServletResponse response,
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

    public static boolean isOptionsRequest(ServletRequest request){
        if(request instanceof HttpServletRequest){
            HttpServletRequest res = (HttpServletRequest) request;
            String method = res.getMethod();
            if(method.equals("OPTIONS")){
                return true;
            }
        }
        return false;
    }
}
