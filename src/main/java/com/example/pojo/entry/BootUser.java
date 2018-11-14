package com.example.pojo.entry;

import java.io.Serializable;

public class BootUser implements Serializable {
    private static final long serialVersionUID = 1961980292810329045L;
    private Integer id;

    private String name;

    private Integer age;

    private Byte six;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Byte getSix() {
        return six;
    }

    public void setSix(Byte six) {
        this.six = six;
    }
}