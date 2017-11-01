package com.example.pojo.dto;

/**
 * Created by dugq on 2017/11/1.
 */
public class ResultBean {
    private String code;
    private String message;

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
