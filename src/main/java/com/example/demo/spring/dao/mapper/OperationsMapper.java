package com.example.demo.spring.dao.mapper;

import com.example.demo.spring.pojo.entry.Operations;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperationsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Operations record);

    int insertSelective(Operations record);

    Operations selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Operations record);

    int updateByPrimaryKey(Operations record);

    List<String> selectPermsListByUrl(String str);

    List<String> selectPermsListById(int id);
}
