package com.example.service;

import com.example.pojo.entry.User;

import java.util.List;

/**
 * Created by dugq on 2017/6/28.
 */
public interface UserService {
    int insert(User user);

    User selectByName(String name);

    List<User> selectAll();
}
