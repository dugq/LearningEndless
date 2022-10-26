package com.example.demo.spring.dao.mapper;

import com.example.demo.spring.pojo.entry.ModuleRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModuleRoleMapper {
    int insert(ModuleRole record);

    int insertSelective(ModuleRole record);
}
