package com.example.demo.spring.dao.mapper;

import com.example.demo.spring.pojo.entry.Module;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModuleMapper {
    int deleteByPrimaryKey(Integer mid);

    int insert(Module record);

    int insertSelective(Module record);

    Module selectByPrimaryKey(Integer mid);

    int updateByPrimaryKeySelective(Module record);

    int updateByPrimaryKey(Module record);
}
