package com.example.pojo.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dugq on 2017/11/1.
 */
public class ResultBean {
    private String code;
    private String message;
    private Map body = new HashMap();

    public Map getBody() {
        return body;
    }

    public void setBody(Map body) {
        this.body = body;
    }

    public void addAttribute(String key, Object value){
        body.put("key",value);
    }
    public void addAttributes(Map map){
        body.putAll(map);
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultBean() {
    }

    public ResultBean(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
