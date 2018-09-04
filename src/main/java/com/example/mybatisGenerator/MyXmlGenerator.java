package com.example.mybatisGenerator;

import com.example.mybatisGenerator.xmlGenrator.*;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.InsertSelectiveElementGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.UpdateByPrimaryKeySelectiveElementGenerator;

import java.lang.reflect.Field;
import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class MyXmlGenerator extends XMLMapperGenerator {
    @Override
    protected XmlElement getSqlMapElement() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
                "Progress.12", table.toString())); //$NON-NLS-1$
        XmlElement answer = new XmlElement("mapper"); //$NON-NLS-1$
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
        answer.addAttribute(new Attribute("namespace", //$NON-NLS-1$
                namespace));

        context.getCommentGenerator().addRootComment(answer);


        addResultMapWithoutBLOBsElement(answer);
        addResultMapWithBLOBsElement(answer);
        addBaseColumnListElement(answer);
        addSelectOneElement(answer);
        addSelectListElement(answer);
        addUpdateByPrimaryKeySelectiveElement(answer);
        addSelectCountElementGenrator(answer);
        addInsertSelectiveElement(answer);
        addDeleteElement(answer);
//        addExampleWhereClauseElement(answer);
//        addMyBatis3UpdateByExampleWhereClauseElement(answer);
//        addBaseColumnListElement(answer);
//        addBlobColumnListElement(answer);
//        addSelectByExampleWithBLOBsElement(answer);
//        addSelectByExampleWithoutBLOBsElement(answer);
//        addSelectByPrimaryKeyElement(answer);
//        addDeleteByPrimaryKeyElement(answer);
//        addDeleteByExampleElement(answer);
//        addInsertElement(answer);
//        addInsertSelectiveElement(answer);
//        addCountByExampleElement(answer);
//         addUpdateByExampleSelectiveElement(answer);
//        addUpdateByExampleWithBLOBsElement(answer);
//        addUpdateByExampleWithoutBLOBsElement(answer);
//        addUpdateByPrimaryKeyWithBLOBsElement(answer);
//        addUpdateByPrimaryKeyWithoutBLOBsElement(answer);

        return answer;
    }

    private void addSelectOneElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new SelectOneElementGenrator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
    private void addDeleteElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new DeleteElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    private void addSelectListElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new SelectListElementGenrator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
    private void addSelectCountElementGenrator(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new SelectCountElementGenrator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
    @Override
    protected void addUpdateByPrimaryKeySelectiveElement(XmlElement parentElement){
        AbstractXmlElementGenerator elementGenerator = new UpdateElementGenrator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
    protected void addInsertSelectiveElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new InertElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
}
