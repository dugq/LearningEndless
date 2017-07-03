package com.example.dao;

import com.example.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by dugq on 2017/6/28.
 */
@Mapper
public interface UserMapper {
    @Insert("Insert into boot_user(name,age,six) values(#{name},#{age},#{six})")
     void insert(User user);
    @Select("select * from boot_user where name = #{name}")
     User selectByName(@Param("name") String name);
    @Select("select * from boot_user")
    List<User> selectAll();
}
