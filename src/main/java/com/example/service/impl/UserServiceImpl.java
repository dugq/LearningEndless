package com.example.service.impl;

import com.example.dao.UserMapper;
import com.example.pojo.User;
import com.example.pojo.annotation.MyAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by dugq on 2017/6/28.
 */
@Service
@Transactional(value = "txManage",rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
public class UserServiceImpl implements com.example.service.UserService {
    @Autowired
    private UserMapper UserMapper;

    @Override
    public void insert(@Valid User user) {
        UserMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public User selectByName(String name) {
        return UserMapper.selectByName(name);
    }

    @Override
    public List<User> selectAll() {
        return UserMapper.selectAll();
    }
}
