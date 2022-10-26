package com.example.demo.spring.service;

import com.example.demo.spring.pojo.entry.User;

import java.util.List;

/**
 * Created by dugq on 2017/6/28.
 */
public interface UserService {
    int deleteByPrimaryKey(Integer uid);

    int insert(User user);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer uid);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByName(String name);

    List<User> selectAll();

     void test(User user1);
}
