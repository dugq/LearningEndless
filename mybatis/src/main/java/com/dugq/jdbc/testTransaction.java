package com.dugq.jdbc;

import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by dugq on 2024/1/16.
 */
public class testTransaction {

    @Resource
    private PrepareStatementTest prepareStatementTest;


    /**
     * 事务以connection 为纬度的，同一个connection中不同的statement归属同一个事务
     * @throws SQLException
     */

    @Test
    public void testTransaction() throws SQLException {
        String selectAll = "select * from user;";
        try {
            String sql = "insert into user (name, age) values('赵六',333)";
            Connection connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            PreparedStatement selectStatement = connection.prepareStatement(selectAll);
            System.out.println("同一连接，不同statement结果：");
            List<User> list = prepareStatementTest.getList(User.class, selectStatement);
            for (User user : list) {
                System.out.println(user);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("不同连接，不同statement结果：");
        prepareStatementTest.testSelectAll();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis?user=root&password=12345678");
    }

}
