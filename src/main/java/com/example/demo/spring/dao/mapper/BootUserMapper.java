package com.example.demo.spring.dao.mapper;

import com.example.demo.spring.pojo.entry.BootUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BootUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BootUser record);

    int insertSelective(BootUser record);

    BootUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BootUser record);

    int updateByPrimaryKey(BootUser record);
}
