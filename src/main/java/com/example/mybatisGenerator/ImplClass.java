package com.example.mybatisGenerator;

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

    private CompilationUnit compilationUnit;


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
        this.addImportedType(new FullyQualifiedJavaType("com.example.BasicComponent.BasicComponent"));
        this.setSuperClass(new FullyQualifiedJavaType("com.example.BasicComponent.BasicComponent"));
        this.addSuperInterface(compilationUnit.getType());
        ((Interface)compilationUnit).getMethods().forEach(method -> {
            if(method.getName().startsWith("select") && ! (method.getReturnType() instanceof Collection)){
                String key = "selectList";
                methodBody(method, key);
            }else if(method.getName().startsWith("select") && (method.getReturnType() instanceof Collection)){
                String key = "selectList";
                methodBody(method, key);
            }else if(method.getName().startsWith("insert")){
                String key = "insert";
                methodBody(method, key);
            }else if(method.getName().startsWith("delete")){
                methodBody(method, "update");
            }else if(method.getName().startsWith("update")){
                methodBody(method, "update");
            }
            addMethod(method);
        });
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

        sb.append("public ");

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
