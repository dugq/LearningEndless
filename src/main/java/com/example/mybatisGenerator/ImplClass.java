package com.example.mybatisGenerator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;

import java.util.*;

import static org.mybatis.generator.api.dom.OutputUtilities.calculateImports;
import static org.mybatis.generator.api.dom.OutputUtilities.newLine;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * Created by dugq on 2018\9\4 0004.
 */
public class ImplClass extends InnerClass implements CompilationUnit {
    /** The imported types. */
    private Set<FullyQualifiedJavaType> importedTypes;

    /** The static imports. */
    private Set<String> staticImports;

    /** The file comment lines. */
    private List<String> fileCommentLines;



    public ImplClass(FullyQualifiedJavaType type) {
        super(type);
        importedTypes = new TreeSet<FullyQualifiedJavaType>();
        fileCommentLines = new ArrayList<String>();
        staticImports = new TreeSet<String>();
    }

    public ImplClass(String typeName) {
        this(new FullyQualifiedJavaType(typeName));
    }

    public ImplClass(CompilationUnit compilationUnit) {
        this(new FullyQualifiedJavaType(compilationUnit.getType().getPackageName()+"."+"impl"+"."+compilationUnit.getType().getShortName()+"Impl"));
        importedTypes.add(compilationUnit.getType());
        FullyQualifiedJavaType importedType = new FullyQualifiedJavaType("cn.com.duiba.activity.custom.center.dao.BaseDao");
        this.addImportedType(importedType);
        this.setSuperClass(importedType);
        this.addSuperInterface(compilationUnit.getType());
        ovverriteMethod();
        ArrayList<Method> methods = (ArrayList<Method>) ((Interface) compilationUnit).getMethods();
        for(int i = 0 ; i < methods.size();i++){
            Method method = methods.get(i);
            Method newM = new Method();
            method.getParameters().forEach(parameter -> newM.addParameter(parameter));
            newM.setVisibility(JavaVisibility.PUBLIC);
            newM.addAnnotation("@Override");
            newM.setReturnType(method.getReturnType());
            newM.setName(method.getName());
            if(newM.getName().startsWith("select") && (newM.getReturnType().getShortName().indexOf("List")!=-1)){
                String key = "selectList";
                methodBody(newM, key);
            }else if(newM.getName().startsWith("select") && (newM.getReturnType().getShortName().indexOf("List")==-1)){
                String key = "selectOne";
                methodBody(newM, key);
            }else if(newM.getName().startsWith("insert")){
                String key = "insert";
                methodBody(newM, key);
            }else if(newM.getName().startsWith("delete")){
                MyInterface myInterface = (MyInterface)compilationUnit;
                IntrospectedTable introspectedTable = myInterface.getIntrospectedTable();
                FullyQualifiedJavaType parameterType = introspectedTable.getRules()
                        .calculateAllFieldsClass();
                if(hasDeleted(introspectedTable)){
                    StringBuilder  sb = new StringBuilder();
                    sb.append(parameterType.getShortName());
                    sb.append(" pojo");
                    sb.append(" = new ");
                    sb.append(parameterType.getShortName());
                    sb.append("();");
                    newM.addBodyLine(sb.toString());
                    newM.addBodyLine("pojo.setId(id);");
                    newM.addBodyLine("pojo.setDeleted(Deleted.DELETED.getCode());");
                    StringBuilder  sb1 = new StringBuilder();
                    sb1.append("return super.update(\"");
                    sb1.append(method.getName()+"\"");
                    sb1.append(",");
                    sb1.append("pojo");
                    sb1.append(");");
                    newM.addBodyLine(sb1.toString());
                }else {
                    StringBuilder  sb = new StringBuilder();
                    sb.append("return super.delete(\"");
                    sb.append(method.getName()+"\"");
                    sb.append(",");
                    sb.append("id");
                    sb.append(");");
                    newM.addBodyLine(sb.toString());
                }
            }else if(newM.getName().startsWith("update")){
                methodBody(newM, "update");
            }
            addMethod(newM);
        }

    }

    private boolean hasDeleted(IntrospectedTable introspectedTable){
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (int i = 0 ; i< allColumns.size(); i++){
            String actualColumnName = allColumns.get(i).getActualColumnName();
            if(actualColumnName.equalsIgnoreCase("deleted")){
                return true;
            }
        }
        return false;
    }

    private void initMethod() {
        FullyQualifiedJavaType importedType2 = new FullyQualifiedJavaType("cn.com.duiba.activity.custom.center.constants.DbSchemaConstants");
        this.addImportedType(importedType2);
        this.addImportedType(new FullyQualifiedJavaType("javax.annotation.PostConstruct"));
        Method init = new Method();
        init.setName("init");
        init.setReturnType(new FullyQualifiedJavaType("void"));
        init.setVisibility(JavaVisibility.PUBLIC);
        init.addBodyLine(" this.databaseSchema = DatabaseSchema.DEVELOPER_APP;");
        init.addAnnotation("@PostConstruct");
        addMethod(init);
    }
    private void ovverriteMethod() {
        FullyQualifiedJavaType importedType2 = new FullyQualifiedJavaType("cn.com.duiba.activity.custom.center.constants.DbSchemaConstants");
        this.addImportedType(importedType2);
        Method init = new Method();
        init.setName("chooseSchema");
        init.setReturnType(new FullyQualifiedJavaType("java.lang.String"));
        init.setVisibility(JavaVisibility.PUBLIC);
        init.addBodyLine(" return DatabaseSchema.DEVELOPER_APP;");
        init.addAnnotation("@Override");
        addMethod(init);
    }

    private void methodBody(Method method, String key) {
        List<Parameter> parameters = method.getParameters();
        if(parameters.size()==1){
            StringBuilder  sb = new StringBuilder();
            sb.append("return super."+key+"(\"");
            sb.append(method.getName()+"\"");
            sb.append(",");
            sb.append(parameters.get(0).getName());
            sb.append(");");
            method.addBodyLine(sb.toString());
        }else if(parameters.size()>1){
            method.addBodyLine("Map map = new HashMap();\n");
            parameters.forEach(parameter -> {
                StringBuilder  sb = new StringBuilder(" map.put(\"");
                sb.append(parameter.getName());
                sb.append("\",");
                sb.append(parameter.getName());
                method.addBodyLine("Map map = new HashMap();\n");
            });
            StringBuilder  sb = new StringBuilder();
            sb.append("return super."+key+"(\"");
            sb.append(method.getName()+"\"");
            sb.append(",");
            sb.append("map");
            sb.append(");");
            method.addBodyLine(sb.toString());
        }
    }

    public Set<FullyQualifiedJavaType> getImportedTypes() {
        return Collections.unmodifiableSet(importedTypes);
    }

    /**
     * Adds the imported type.
     *
     * @param importedType
     *            the imported type
     */
    public void addImportedType(String importedType) {
        addImportedType(new FullyQualifiedJavaType(importedType));
    }

    @Override
    public String getFormattedContent() {
        StringBuilder sb = new StringBuilder();

        for (String fileCommentLine : fileCommentLines) {
            sb.append(fileCommentLine);
            newLine(sb);
        }

        if (stringHasValue(getType().getPackageName())) {
            sb.append("package "); //$NON-NLS-1$
            sb.append(getType().getPackageName());
            sb.append(';');
            newLine(sb);
            newLine(sb);
        }

        for (String staticImport : staticImports) {
            sb.append("import static "); //$NON-NLS-1$
            sb.append(staticImport);
            sb.append(';');
            newLine(sb);
        }

        if (staticImports.size() > 0) {
            newLine(sb);
        }

        Set<String> importStrings = calculateImports(importedTypes);
        for (String importString : importStrings) {
            sb.append(importString);
            newLine(sb);
        }

        if (importStrings.size() > 0) {
            newLine(sb);
        }

        sb.append(super.getFormattedContent(0, this));

        return sb.toString();
    }


    @Override
    public Set<String> getStaticImports() {
        return null;
    }

    @Override
    public boolean isJavaInterface() {
        return false;
    }

    @Override
    public boolean isJavaEnumeration() {
        return false;
    }

    @Override
    public void addImportedType(FullyQualifiedJavaType importedType) {
        importedTypes.add(importedType);
    }

    @Override
    public void addImportedTypes(Set<FullyQualifiedJavaType> importedTypes1) {
        importedTypes1.forEach(importedType-> importedTypes.add(importedType));
    }

    @Override
    public void addStaticImport(String staticImport) {

    }

    @Override
    public void addStaticImports(Set<String> staticImports) {

    }

    @Override
    public void addFileCommentLine(String commentLine) {

    }

    @Override
    public List<String> getFileCommentLines() {
        return null;
    }
}
