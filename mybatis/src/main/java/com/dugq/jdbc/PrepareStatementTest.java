package com.dugq.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dugq on 2024/1/16.
 */
@SpringBootTest
@Slf4j
public class PrepareStatementTest {

    @Test
    public void testSelectAll()  {
        String sql = "select * from user";
        List<User> users = selectList(sql, User.class);
        for (User user : users) {
            System.out.println(user);
        }
    }

    public  <T> List<T> selectList(String sql,Class<T> clazz,Object... params) {
        try(
                Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis?user=root&password=12345678");
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            return getList(clazz, preparedStatement, params);
        }catch (Exception e){
            log.error("sql error",e);
            return null;
        }
    }

    public  <T> List<T> getList(Class<T> clazz, PreparedStatement preparedStatement, Object... params) throws SQLException, InstantiationException, IllegalAccessException {
        if (params.length>0){
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i+1, params[i]);
            }
        }
        ResultSet resultSet = preparedStatement.executeQuery();
        int columnCount = resultSet.getMetaData().getColumnCount();
        List<T> list = new ArrayList<>();
        while (resultSet.next()){
            T obj = clazz.newInstance();
            for (int i=0; i<columnCount; i++){
                String columnLabel = resultSet.getMetaData().getColumnLabel(i+1);
                try {
                    Field field = clazz.getDeclaredField(columnLabel);
                    Class<?> type = field.getType();
                    field.setAccessible(true);
                    field.set(obj,resultSet.getObject(i+1, type));
                }catch (Exception e){
                  e.printStackTrace();
                }
            }
            list.add(obj);
        }
        return list;
    }

    @Test
    public void testGenericId(){
        try {
            String sql = "insert into user (name, age) values('赵六',333)";
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis?user=root&password=12345678");
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate(sql);
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            System.out.println("generatedKeys: " + generatedKeys.getLong(1));
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    @Test
    public void testBatchInsert(){
        try {
            String sql = "insert into user (name, age) values (?,?)";
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis?rewriteBatchedStatements=true&user=root&password=12345678");
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i =0 ; i< 100; i++){
                preparedStatement.setString(1,"batchUser"+i);
                preparedStatement.setInt(2,i*13 % 50);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
            preparedStatement.close();
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testDelete(){
        try {
            String sql = "delete from user;";
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis?user=root&password=12345678");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate(sql);
            preparedStatement.close();
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        testSelectAll();
    }
}
