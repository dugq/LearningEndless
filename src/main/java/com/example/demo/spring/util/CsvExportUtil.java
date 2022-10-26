package com.example.demo.spring.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2019-03-16.
 */
public class CsvExportUtil {
    private static final String SEPARATOR = ",";
    private static final Logger logger = LoggerFactory.getLogger(CsvExportUtil.class);

    private CsvExportUtil(){}

    public static <T> void export(HttpServletResponse response, String fileName, List<T> sourceList , Class<T> clazz) throws IOException {
        String fileN = null;
        try {
            fileN = new String(fileName.getBytes("UTF-8"), "ISO_8859_1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("系统错误");
        }
        response.setContentType("text/csv");
        response.setContentType("application/x-xls;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileN + ".csv");
        export(response.getOutputStream(),sourceList,clazz);
    }


    public static <T> void  export(OutputStream out, List<T> sourceList, Class<T> clazz) throws IOException {
        if (CollectionUtils.isEmpty(sourceList)){
            return;
        }
        try (
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out))
        ){
            writeExcelEncodingSign(bufferedWriter);
            List<Field> headField = getCsvFieldsByReflectAndWriteHead2Stream(clazz, bufferedWriter);
            writeExcelContent2Stream(sourceList, bufferedWriter, headField);
        }catch (Exception e){
            logger.error("",e);
        }finally {
            out.close();
        }
    }

    private static <T> void writeExcelContent2Stream(List<T> sourceList, BufferedWriter bufferedWriter, List<Field> headField) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
        for (T source : sourceList){
            StringBuilder line = new StringBuilder();
            for (Field field : headField){
                String property = BeanUtils.getProperty(source, field.getName());
                if(Objects.isNull(property)){
                    line.append(" ");
                }else{
                    line.append(property);
                }
                line.append(SEPARATOR);
            }
            line.deleteCharAt(line.length()-1);
            bufferedWriter.newLine();
            bufferedWriter.write(line.toString());
            bufferedWriter.flush();
        }
    }

    private static <T> List<Field> getCsvFieldsByReflectAndWriteHead2Stream(Class<T> clazz, BufferedWriter bufferedWriter) throws IOException {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> headField = new ArrayList<>();
        StringBuilder title = new StringBuilder();
        for (Field field : fields){
            CsvFiled annotation = field.getAnnotation(CsvFiled.class);
            if(Objects.nonNull(annotation)){
                headField.add(field);
                title.append(annotation.value());
                title.append(SEPARATOR);
            }
        }
        title.deleteCharAt(title.length()-1);
        bufferedWriter.write(title.toString());
        return headField;
    }

    /**
     * 解决excel 编码问题
     * @param bufferedWriter
     * @throws IOException
     */
    private static void writeExcelEncodingSign(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}));
        bufferedWriter.flush();
    }
}
