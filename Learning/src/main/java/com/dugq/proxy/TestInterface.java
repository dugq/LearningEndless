package com.dugq.proxy;

import com.dugq.base.User;

import java.util.List;

/**
 * Created by dugq on 2024/1/17.
 */
public interface TestInterface {

    List<User> selectAll();

    User selectById(Long id);



}
