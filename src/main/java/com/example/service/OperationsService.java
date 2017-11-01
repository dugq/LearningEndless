package com.example.service;

import com.example.pojo.entry.Operations;

import java.util.List;

/**
 * Created by dugq on 2017/11/1.
 */
public interface OperationsService {
    int deleteByPrimaryKey(Integer id);

    int insert(Operations record);

    int insertSelective(Operations record);

    Operations selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Operations record);

    int updateByPrimaryKey(Operations record);

    List<String> selectPermsListByUrl(String str);
}
