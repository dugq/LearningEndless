package com.example.mybatisGenerator.daoGenerator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class DeleteGenerator extends
        AbstractJavaMapperMethodGenerator {

    public DeleteGenerator() {
        super();
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = new Method();

        method.setReturnType(new FullyQualifiedJavaType("java.lang.Integer"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("delete");

        FullyQualifiedJavaType parameterType = introspectedTable.getRules()
                .calculateAllFieldsClass();

        importedTypes.add(parameterType);
        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        addMapperAnnotations(interfaze, method);
        method.addParameter(new Parameter(new FullyQualifiedJavaType("java.lang.Long"), "id"));




        if(hasDeleted()){
            method.addJavaDocLine("/**");
            method.addJavaDocLine("*");
            method.addJavaDocLine("* 软删");
            method.addJavaDocLine("*/");
    }else {

            method.addJavaDocLine("/**");
            method.addJavaDocLine("*");
            method.addJavaDocLine("* 物理删除");
            method.addJavaDocLine("*/");
    }

        if (context.getPlugins().clientInsertSelectiveMethodGenerated(
                method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    private boolean hasDeleted(){
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for(int i = 0 ; i<allColumns.size() ; i++){
            if(allColumns.get(i).getActualColumnName().equalsIgnoreCase("deleted")){
                return true;
            }
        }
        return false;
    }

    public void addMapperAnnotations(Interface interfaze, Method method) {
    }
}
