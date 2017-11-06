package com.example.service.impl;

import com.example.dao.mapper.OperationModuleMapper;
import com.example.dao.mapper.OperationsMapper;
import com.example.pojo.entry.Operations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dugq on 2017/11/1.
 */
@Service
public class OperationsServiceImpl implements com.example.service.OperationsService {
    @Autowired
    private OperationModuleMapper operationModuleMapper;
    @Autowired
    private OperationsMapper operationsMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return operationsMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Operations record) {
        return operationsMapper.insert(record);
    }

    @Override
    public int insertSelective(Operations record) {
        return operationsMapper.insertSelective(record);
    }

    @Override
    public Operations selectByPrimaryKey(Integer id) {
        return operationsMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(Operations record) {
        return operationsMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Operations record) {
        return operationsMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<String> selectPermsListByUrl(String str) {
        return operationsMapper.selectPermsListByUrl(str);
    }

    @Override
    public List<String> selectPermsListByUser(int str) {
        return operationsMapper.selectPermsListById(str);
    }


}
