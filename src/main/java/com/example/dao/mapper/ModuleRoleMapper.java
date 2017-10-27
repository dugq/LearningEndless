package com.example.dao.mapper;

import com.example.pojo.entry.ModuleRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModuleRoleMapper {
    int insert(ModuleRole record);

    int insertSelective(ModuleRole record);
}