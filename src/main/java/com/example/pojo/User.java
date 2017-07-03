package com.example.pojo;

import com.example.Six;
import com.example.pojo.annotation.MyAnnotation.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


/**
 * Created by dugq on 2017/6/22.
 */
public class User {
    private int id;
    @NotBlank(groups = NAME.class,message = "姓名不能为空")
    private String name;
    @NotNull(message = "性别不能为空")
    private byte six;
    @Min(value = 0 ,groups = {AGE.class,NAME.class},message = "年龄必须在大于0")
    @Max(value = 100 ,groups = AGE.class,message = "年龄必须在小于100")
    private int age;

    public User() {
    }

    public User(int id, String name, int six, int age) {
        this.id = id;
        this.name = name;
        this.six = (byte) six;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getSix() {
        return six;
    }

    public void setSix(byte six) {
        this.six = six;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "my name is "+name;
    }
}

