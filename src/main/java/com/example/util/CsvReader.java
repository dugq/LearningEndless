package com.example.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by dugq on 2019-07-19.
 */
public class CsvReader {

    public static <T> List<T> readerFile(File file, Class<T> clazz) {
        try (
                BufferedReader reader = new BufferedReader(new FileReader(file))
        ) {
            return getList(clazz, reader);
        } catch (FileNotFoundException e) {
            //读文件失败
            e.printStackTrace();
        } catch (IOException e) {
            //读文件流失败
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            //创建对象失败
            e.printStackTrace();
        } catch (InstantiationException e) {
            //创建对象失败
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static <T> List<T> readerIO(InputStream in, Class<T> clazz) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))
        ) {
            return getList(clazz, reader);
        } catch (FileNotFoundException e) {
            //读文件失败
            e.printStackTrace();
        } catch (IOException e) {
            //读文件流失败
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            //创建对象失败
            e.printStackTrace();
        } catch (InstantiationException e) {
            //创建对象失败
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private static <T> List<T> getList(Class<T> clazz, BufferedReader reader) throws IOException, InstantiationException, IllegalAccessException {
        String[] headers = null;
        LinkedList<T> list = new LinkedList<>();


        while (reader.ready()) {
            String s = reader.readLine();
            if (Objects.isNull(headers)) {
                if(s.charAt(0) == '\uFEFF'){
                    s = s.substring(1);
                }
                headers = s.split(",");
                continue;
            }
            String[] columns = s.split(",");
            T obj = clazz.newInstance();
            for (int i = 0 ; i < columns.length; i++){
                String header = headers[i];
                String column = columns[i];
                Field field = getFieldByAnnotation(clazz, header);
                if(Objects.isNull(field)){
                    continue;
                }
                field.setAccessible(true);
                if(field.getType().isPrimitive()){
                    if(NumberUtils.isNumber(column)){
                        field.set(obj,Long.valueOf(column));
                    }
                }else{
                    field.set(obj,column);
                }
            }
            list.add(obj);
        }
        return list;
    }


    private static Field getFieldByAnnotation(Class clazz,String header){
        Field[] fields = clazz.getDeclaredFields();
        for (Field field :fields) {
            CsvFiled annotation = field.getAnnotation(CsvFiled.class);
            try {
                if(Objects.nonNull(annotation) && StringUtils.equalsIgnoreCase(new String(annotation.value().getBytes("utf-8")),new String(header.getBytes("utf-8")))){
                    return field;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
