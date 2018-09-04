package com.example.mybatisGenerator.xmlGenrator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class SelectCountElementGenrator extends
        AbstractXmlElementGenerator {

    private Set<String> ingoreClumns = new HashSet<>();

    {
        ingoreClumns.add("id");
        ingoreClumns.add("gmt_create");
        ingoreClumns.add("gmt_modified");

    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select");

        answer.addAttribute(new Attribute("id","selectCount"));

        answer.addAttribute(new Attribute("resultType","int"));

        answer.addElement(new TextElement("select count(*)"));
        answer.addElement(new TextElement("from "+introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        addWhere(answer);
        parentElement.addElement(answer);
    }

    public void addWhere(XmlElement parentElement){
        XmlElement where = new XmlElement("where");
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        allColumns.forEach(column->{
            String clumnName = MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(column);
            if(ingoreClumns.contains(clumnName)){
                return;
            }
            XmlElement ifElement = new XmlElement("if");
            ifElement.addAttribute(new Attribute("test",column.getJavaProperty()+"!=null"));
            ifElement.addElement(new TextElement(" and " + clumnName +"=#{" + column.getJavaProperty()+"}" ));
            where.addElement(ifElement);
        });
        parentElement.addElement(where);
    }
}
