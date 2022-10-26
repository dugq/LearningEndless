package com.example.demo.spring.service.impl;

import com.example.demo.spring.pojo.entry.User;
import com.example.demo.spring.service.OperationsService;
import com.example.demo.spring.service.base.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.spring.dao.mapper.UserMapper;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

/**
 * Created by dugq on 2017/6/28.
 */
@Service
@Transactional
public class UserServiceImpl extends BasicService implements com.example.demo.spring.service.UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OperationsService operationsService;


    @Override
    public int deleteByPrimaryKey(Integer uid) {
        return userMapper.deleteByPrimaryKey(uid);
    }
    @Override
    public int insert(User record) {
        return userMapper.insert(record);
    }
    @Override
    public int insertSelective(User record) {
        return userMapper.insertSelective(record);
    }
    @Override
    public User selectByPrimaryKey(Integer uid) {
        return userMapper.selectByPrimaryKey(uid);
    }
    @Override
    public int updateByPrimaryKeySelective(User record) {
        return userMapper.updateByPrimaryKeySelective(record);
    }
    @Override
    public int updateByPrimaryKey(User record) {
        return userMapper.updateByPrimaryKey(record);
    }

    @Override
    public User selectByName(String name) {
        return userMapper.selectByName(name);
    }

    @Override
    public List<User> selectAll() {
        return null;
    }

    @Override
    public void test(User user1) {
        userMapper.updateByPrimaryKey(user1);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
