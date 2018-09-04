package com.example.mybatisGenerator;

import com.example.mybatisGenerator.daoGenerator.*;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.InsertSelectiveMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.UpdateByPrimaryKeySelectiveMethodGenerator;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class MyJavaMapperGenerator extends JavaMapperGenerator {

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface = introspectedTable
                .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                    .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }

         addSelectOne(interfaze);
         addSelectList(interfaze);
         addUpdateByPrimaryKeySelectiveMethod(interfaze);
         addSelectCount(interfaze);
         addInsertSelectiveMethod(interfaze);
         addDeleted(interfaze);
//        addCountByExampleMethod(interfaze);
//        addDeleteByExampleMethod(interfaze);
//        addDeleteByPrimaryKeyMethod(interfaze);
//        addInsertMethod(interfaze);
//        addInsertSelectiveMethod(interfaze);
//        addSelectByExampleWithBLOBsMethod(interfaze);
//        addSelectByExampleWithoutBLOBsMethod(interfaze);
//        addSelectByPrimaryKeyMethod(interfaze);
//        addUpdateByExampleSelectiveMethod(interfaze);
//        addUpdateByExampleWithBLOBsMethod(interfaze);
//        addUpdateByExampleWithoutBLOBsMethod(interfaze);
//        addUpdateByPrimaryKeyWithBLOBsMethod(interfaze);
//        addUpdateByPrimaryKeyWithoutBLOBsMethod(interfaze);

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().clientGenerated(interfaze, null,
                introspectedTable)) {
            answer.add(interfaze);
        }

        List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }

        return answer;
    }

    private void addSelectOne(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new SelectOneGenerator(false);
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
    private void addSelectList(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new SelectListGenerator(false);
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
    protected void addInsertSelectiveMethod(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new InsertGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
    private void addSelectCount(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new SelectCountGenerator(false);
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }

    private void addDeleted(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new DeleteGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }

    protected void addUpdateByPrimaryKeySelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
            UpdateByPrimaryKeySelectiveMethodGenerator methodGenerator = new UpdateByPrimaryKeySelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
            List<Method> methods = interfaze.getMethods();
            methods.forEach(method -> {
                if(Objects.equals(method.getName(),"updateByPrimaryKeySelective")){
                    method.setName("update");
                }

            });
        }
    }
    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return new MyXmlGenerator();
    }
}
