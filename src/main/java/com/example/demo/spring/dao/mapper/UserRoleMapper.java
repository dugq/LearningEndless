package com.example.demo.spring.dao.mapper;

import com.example.demo.spring.pojo.entry.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper {
    int insert(UserRole record);

    int insertSelective(UserRole record);
}
