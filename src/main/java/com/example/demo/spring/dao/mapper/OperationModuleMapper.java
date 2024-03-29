package com.example.demo.spring.dao.mapper;

import com.example.demo.spring.pojo.entry.OperationModule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OperationModule record);

    int insertSelective(OperationModule record);

    OperationModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OperationModule record);

    int updateByPrimaryKey(OperationModule record);
}
