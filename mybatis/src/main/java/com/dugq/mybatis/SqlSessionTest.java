package com.dugq.mybatis;

import com.dugq.jdbc.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created by dugq on 2024/1/23.
 */
@SpringBootTest
public class SqlSessionTest {

    @Test
    public void testSqlSession(){
        SqlSession sqlSession =  new  SqlSessionFactoryBuilder().buildSqlSession();
        User user = sqlSession.selectOne("com.dugq.mybatis.UserMapper.selectOne",1);
        System.out.println(user);
    }

}
