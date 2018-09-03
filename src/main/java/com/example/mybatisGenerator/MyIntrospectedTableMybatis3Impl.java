package com.example.mybatisGenerator;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.config.PropertyRegistry;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dugq on 2018/9/3 0003.
 */
public class MyIntrospectedTableMybatis3Impl extends IntrospectedTableMyBatis3Impl {
    @Override
    protected void calculateModelAttributes() {
        String pakkage = calculateJavaModelPackage();

        StringBuilder sb = new StringBuilder();
        sb.append(pakkage);
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Key"); //$NON-NLS-1$
        setPrimaryKeyType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Entity");
        setBaseRecordType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("WithBLOBs"); //$NON-NLS-1$
        setRecordWithBLOBsType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Example"); //$NON-NLS-1$
        setExampleType(sb.toString());
    }
    @Override
    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        List<GeneratedJavaFile> answer = super.getGeneratedJavaFiles();
        for (AbstractJavaGenerator javaGenerator : clientGenerators) {
            List<CompilationUnit> compilationUnits = javaGenerator
                    .getCompilationUnits();
            for (CompilationUnit compilationUnit : compilationUnits) {
                ImplClass newUnit = new ImplClass(compilationUnit);
                newUnit.addImportedTypes(compilationUnit.getImportedTypes());
                GeneratedJavaFile gjf = new GeneratedJavaFile(newUnit,
                        context.getJavaClientGeneratorConfiguration()
                                .getTargetProject(),
                        context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                        context.getJavaFormatter());
                answer.add(gjf);
            }
        }
        return answer;
    }
    @Override
    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>();

        if (xmlMapperGenerator != null) {
            Document document = xmlMapperGenerator.getDocument();
            List<Attribute> attributes = document.getRootElement().getAttributes();
            for(int i =0; i< attributes.size();i++){
                Attribute attribute = attributes.get(i);
                if(attribute.getName().equalsIgnoreCase("namespace")){
                    String value = attribute.getValue();
                    String pre = value.substring(0, value.lastIndexOf("."));
                    String fix =  value.substring(value.lastIndexOf(".")+1);
                    String namespcace = pre + ".impl." + fix + "Impl";
                    try {
                        Class<? extends Attribute> aClass = attribute.getClass();
                        Field field = aClass.getDeclaredField("value");
                        field.setAccessible(true);
                        field.set(attribute,namespcace);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            GeneratedXmlFile gxf = new GeneratedXmlFile(document,
                    getMyBatis3XmlMapperFileName(), getMyBatis3XmlMapperPackage(),
                    context.getSqlMapGeneratorConfiguration().getTargetProject(),
                    true, context.getXmlFormatter());
            if (context.getPlugins().sqlMapGenerated(gxf, this)) {
                answer.add(gxf);
            }
        }

        return answer;
    }


}
