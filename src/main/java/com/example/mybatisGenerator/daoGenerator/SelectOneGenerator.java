package com.example.mybatisGenerator.daoGenerator;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class SelectOneGenerator  extends
        AbstractJavaMapperMethodGenerator {
    private boolean isSimple;

    public SelectOneGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }


    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);

        FullyQualifiedJavaType returnType = introspectedTable.getRules()
                .calculateAllFieldsClass();
        method.setReturnType(returnType);
        importedTypes.add(returnType);

        method.setName("selectOne");
        importedTypes.add(new FullyQualifiedJavaType("java.lang.Long"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("java.lang.Long"),"id"));
        method.addJavaDocLine("/**");
        method.addJavaDocLine("根据id查询Entity");
        method.addJavaDocLine("*");
        method.addJavaDocLine("*/");
        addMapperAnnotations(interfaze, method);

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        if (context.getPlugins().clientSelectByPrimaryKeyMethodGenerated(
                method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    public void addMapperAnnotations(Interface interfaze, Method method) {
    }
}
