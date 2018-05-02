package com.example.util;

import com.example.dao.mapper.BootUserMapper;
import com.example.pojo.entry.BootUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by dugq on 2018/4/18.
 */
@Component
public class MyCommandLineRunner  implements CommandLineRunner {
    @Autowired
    private BootUserMapper bootUserMapper;

    @Override
    public void run(String... var1) throws Exception{
        System.out.println("This will be execute when the project was started!");
//        BootUser bootUser = bootUserMapper.selectByPrimaryKey(1);
//        System.out.println(bootUser);
    }
}
