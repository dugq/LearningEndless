package com.dugq.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by dugq on 2024/1/16.
 */
@Slf4j
@SpringBootTest
public class JDBCFirstTest {
    @Test
    public void testJdbcSelect() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis", "root", "12345678");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from user;");
        int size = resultSet.getRow();

        // 获取表和列的信息
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()){
            User user = new User();
//            Object obj = clazz.newInstance();
//            for (int i =0 ; i < columnCount;i++){
//                String columnName = metaData.getColumnName(i + 1);
//                String propSetterName = "set"+columnName.substring(0,0).toUpperCase()+columnName.substring(1,columnName.length()-1);
//                Method method = clazz.getDeclaredMethod(propSetterName);
//                需要根据具体的类型进行反射
//                method.invoke(obj,resultSet.getInt());
//            }
            long id = resultSet.getLong(1);
            String name = resultSet.getString(2);
            int age = resultSet.getInt(3);
            user.setId(id);
            user.setName(name);
            user.setAge(age);
            log.info("user : {}",user);
        }
        System.out.println("size: "+size);
        resultSet.close();
        statement.close();
        connection.close();
    }

    @Test
    public void testJdbcCreate() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis", "root", "12345678");
        Statement statement = connection.createStatement();
        int resultSet = statement.executeUpdate("CREATE TABLE `user`  (\n" +
                "  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(255) NULL,\n" +
                "  `age` int(0) NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ");");
        log.info("result {}",resultSet);
        connection.commit();
        statement.close();
        connection.close();
    }

    @Test
    public void testJdbcInsert() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis", "root", "12345678");
        Statement statement = connection.createStatement();
        int resultSet = statement.executeUpdate("insert user (`name`,`age`)values (\"王五\",20);");
        log.info("result {}",resultSet);
        if (!connection.getAutoCommit()){
            connection.commit();
        }
        statement.close();
        connection.close();
    }

}
