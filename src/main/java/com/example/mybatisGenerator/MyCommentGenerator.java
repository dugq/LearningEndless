package com.example.mybatisGenerator;
import static org.mybatis.generator.internal.util.StringUtility.isTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;

public class MyCommentGenerator implements CommentGenerator{
    private Properties properties;
    private Properties systemPro;
    private boolean suppressDate;
    private boolean suppressAllComments;
    private String currentDateStr;

    public MyCommentGenerator() {
        super();
        properties = new Properties();
        systemPro = System.getProperties();
        suppressDate = false;
        suppressAllComments = false;
        currentDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
    }

    public void addJavaFileComment(CompilationUnit compilationUnit) {
        return;
    }

    /**
     * Adds a suitable comment to warn users that the element was generated, and
     * when it was generated.
     */
    public void addComment(XmlElement xmlElement) {
        return;
    }

    public void addRootComment(XmlElement rootElement) {
        return;
    }

    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);

        suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));

        suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
    }

    /**
     * This method adds the custom javadoc tag for. You may do nothing if you do
     * not wish to include the Javadoc tag - however, if you do not include the
     * Javadoc tag then the Java merge capability of the eclipse plugin will
     * break.
     *
     * @param javaElement
     *            the java element
     */
    protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
        javaElement.addJavaDocLine(" *");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        sb.append(MergeConstants.NEW_ELEMENT_TAG);
        if (markAsDoNotDelete) {
            sb.append(" do_not_delete_during_merge");
        }
        String s = getDateString();
        if (s != null) {
            sb.append(' ');
            sb.append(s);
        }
        javaElement.addJavaDocLine(sb.toString());
    }

    /**
     * This method returns a formated date string to include in the Javadoc tag
     * and XML comments. You may return null if you do not want the date in
     * these documentation elements.
     *
     * @return a string representing the current timestamp, or null
     */
    protected String getDateString() {
        String result = null;
        if (!suppressDate) {
            result = currentDateStr;
        }
        return result;
    }

    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        innerClass.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append(" ");
        sb.append(getDateString());
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        innerClass.addJavaDocLine(" */");
    }

    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        innerEnum.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        innerEnum.addJavaDocLine(sb.toString().replace("\n", " "));
        innerEnum.addJavaDocLine(" */");
    }

    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {

        if(Objects.equals(introspectedColumn.getJdbcTypeName(),"DATETIME")||Objects.equals(introspectedColumn.getJdbcTypeName(),"DATE" )|| Objects.equals(introspectedColumn.getJdbcTypeName(),"TIME" )){
            field.addJavaDocLine("@JsonFormat(pattern=\"yyyy-MM-dd HH:mm:ss\",timezone = \"GMT+8\")");
            field.addJavaDocLine("@DateTimeFormat(pattern=\"yyyy-MM-dd\")");
        }
//        if(Objects.equals(introspectedColumn.getJdbcTypeName(),"VARCHAR") || Objects.equals(introspectedColumn.getJdbcTypeName(),"CHAR")){
//            field.addJavaDocLine("@Max(value = "+introspectedColumn.getLength()+",message=\""+introspectedColumn.getRemarks()+"最大长度为："+introspectedColumn.getLength()+"\")");
//        }
//        if(!introspectedColumn.isNullable()){
//            field.addJavaDocLine("@NotNull");
//        }
        if (StringUtils.isBlank(introspectedColumn.getRemarks())) {
            return;
        }
        StringBuilder sb = new StringBuilder();
//        sb.append("@ApiModelProperty(value = \"");
//        sb.append(introspectedColumn.getRemarks());
//        sb.append("\")");
//        field.addJavaDocLine(sb.toString().replace("\n", " "));
        field.addJavaDocLine("//"+introspectedColumn.getRemarks());
    }

    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        field.addJavaDocLine(sb.toString().replace("\n", " "));
        field.addJavaDocLine(" */");
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
//        topLevelClass.addJavaDocLine("@ApiModel");
        String remarks = introspectedTable.getRemarks();
        if(StringUtils.isBlank(remarks)){
            return;
        }
        StringBuilder sb = new StringBuilder();
        topLevelClass.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(remarks);
        topLevelClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * @author ");
        sb.append(systemPro.getProperty("user.name"));
        sb.append(" ");
        sb.append(currentDateStr);
        topLevelClass.addJavaDocLine(" */");
    }

    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        method.addJavaDocLine("/**");
        addJavadocTag(method, false);
        method.addJavaDocLine(" */");
    }

    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
       return;
    }

    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        return;
    }

    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        innerClass.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * @author ");
        sb.append(systemPro.getProperty("user.name"));
        sb.append(" ");
        sb.append(currentDateStr);
        innerClass.addJavaDocLine(" */");
    }
}
