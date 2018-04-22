package com.example.mybatisGenerator;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2018/4/16.
 */
public class MyPluginAdapter extends PluginAdapter {

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
//        topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");
//        topLevelClass.addImportedType("io.swagger.annotations.ApiModel");
//        topLevelClass.addImportedType("javax.validation.constraints.Max");
//        topLevelClass.addImportedType("javax.validation.constraints.NotNull");
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if(Objects.equals(introspectedColumn.getJdbcTypeName(),"DATETIME")){
            FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.time.LocalDateTime");
            introspectedColumn.setFullyQualifiedJavaType(type);
            field.setType(type);
            topLevelClass.addImportedType("org.springframework.format.annotation.DateTimeFormat");
            topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");
        }else if (Objects.equals(introspectedColumn.getJdbcTypeName(),"DATE" )){
            topLevelClass.addImportedType("org.springframework.format.annotation.DateTimeFormat");
            topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");
            FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.time.LocalDate");
            introspectedColumn.setFullyQualifiedJavaType(type);
            field.setType(type);
        }else if (Objects.equals(introspectedColumn.getJdbcTypeName(),"TIME" )){
            topLevelClass.addImportedType("org.springframework.format.annotation.DateTimeFormat");
            topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");
            FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.time.LocalTime");
            introspectedColumn.setFullyQualifiedJavaType(type);
            field.setType(type);
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
