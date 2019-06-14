package com.example.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by dugq on 2019-03-16.
 */
@Documented
@Target({ FIELD})
@Retention(RUNTIME)
public @interface CsvFiled {

    String value();
}
