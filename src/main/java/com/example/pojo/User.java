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
    @NotNull(groups = NAME.class)
    @NotBlank(groups = NAME.class,message = "姓名不鞥你为空")
    private String name;
    @NotNull
    private String six;
    @Min(value = 0 ,groups = {AGE.class,NAME.class})
    @Max(value = 100 ,groups = AGE.class)
    private int age;

    public User() {
    }

    public User(int id, String name, String six, int age) {
        this.id = id;
        this.name = name;
        this.six = six;
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

    public String getSix() {
        return six;
    }

    public void setSix(String six) {
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

