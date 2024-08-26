package com.dugq.proxy;

import com.dugq.base.User;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dugq on 2024/1/17.
 */
public class TestClass implements TestInterface{
    @Override
    public List<User> selectAll() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("foo", "bar",1));
        users.add(new User("tom", "tom-psd",2));
        return users;
    }

    @Override
    public User selectById(Long id) {
        return new User("jack", "jack-psd", id.intValue());
    }

    @Override
    public Mono<User> findById(Long id) {
        return null;
    }
}
