package com.example.service.impl;

import com.example.dao.UserMapper;
import com.example.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dugq on 2017/6/28.
 */
@Service
public class UserServiceImpl implements com.example.service.UserService {
    @Autowired
    private UserMapper UserMapper;

    @Override
    public void insert(User user) {
        UserMapper.insert(user);
    }

    @Override
    public User selectByName(String name) {
        return UserMapper.selectByName(name);
    }

    @Override
    public List<User> selectAll() {
        return UserMapper.selectAll();
    }
}
