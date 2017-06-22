package com.example.pojo;

import com.example.Six;

/**
 * Created by dugq on 2017/6/22.
 */
public class User {
    private int id;
    private String name;
    private Six six;
    private int age;

    public User() {
    }

    public User(int id, String name, Six six, int age) {
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

    public Six getSix() {
        return six;
    }

    public void setSix(Six six) {
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

