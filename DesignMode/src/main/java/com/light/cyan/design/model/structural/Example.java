package com.light.cyan.design.model.structural;

import org.junit.Test;

import java.sql.*;

public class Example {

    @Test
    public void test() throws SQLException {
        // 经典桥接模式
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "user", "password");// 执行SQL操作，与具体数据库的实现解耦
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM users");
    }

}
