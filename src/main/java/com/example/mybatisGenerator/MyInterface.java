package com.example.mybatisGenerator;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class MyInterface extends Interface {
    IntrospectedTable introspectedTable;

    public MyInterface(FullyQualifiedJavaType type,IntrospectedTable introspectedTable) {
        super(type);
        this.introspectedTable = introspectedTable;
    }

    public MyInterface(String type) {
        super(type);
    }

    public IntrospectedTable getIntrospectedTable() {
        return introspectedTable;
    }
}
