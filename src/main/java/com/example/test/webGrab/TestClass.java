package com.example.test.webGrab;

import com.example.demo.spring.util.CsvFiled;
import lombok.Data;

/**
 * Created by dugq on 2019-05-13.
 */
@Data
public class TestClass {
    @CsvFiled("日期")
    private String date;
    @CsvFiled("app名称")
    private String appName;
    @CsvFiled("开发者")
    private String developer;
    @CsvFiled("介绍")
    private String desc;
}
