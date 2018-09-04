package com.example.mybatisGenerator.daoGenerator;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class SelectListGenerator extends
        AbstractJavaMapperMethodGenerator {
    private boolean isSimple;

    public SelectListGenerator(boolean isSimple) {
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
        method.setReturnType(new FullyQualifiedJavaType("java.util.List<"+returnType.getShortName()+">"));
        importedTypes.add(returnType);

        method.setName("selectList");
        importedTypes.add(new FullyQualifiedJavaType("java.util.List"));
        method.addParameter(new Parameter(returnType,"pojo"));
        method.addJavaDocLine("/**");
        method.addJavaDocLine("*根据Entity查询");
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
