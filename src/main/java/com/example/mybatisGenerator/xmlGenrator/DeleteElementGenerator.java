package com.example.mybatisGenerator.xmlGenrator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.ibatis2.Ibatis2FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.List;

/**
 * Created by dugq on 2018/9/4 0004.
 */
public class DeleteElementGenerator extends
        AbstractXmlElementGenerator {

    public DeleteElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
       List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();

        XmlElement answer = new XmlElement("delete"); //$NON-NLS-1$

        answer.addAttribute(new Attribute(
                "id", "delete")); //$NON-NLS-1$
        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();

        if(hasDeleted(allColumns)){
            sb.append("update  "); //$NON-NLS-1$
            sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
            sb.append(" set delete = 1");
            sb.append(" where id = #{id}");
       }else{
            sb.append("delete from "); //$NON-NLS-1$
            sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
            sb.append(" where id = #{id}");
       }




        answer.addElement(new TextElement(sb.toString()));
        parentElement.addElement(answer);

    }

    private boolean hasDeleted(List<IntrospectedColumn> allColumns){
        for(int i = 0 ; i<allColumns.size() ; i++){
            if(allColumns.get(i).getActualColumnName().equalsIgnoreCase("deleted")){
                return true;
            }
        }
        return false;
    }
}