package com.example.demo.spring.pojo.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by dugq on 2017/7/11.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MyValidator {
    Class<?>[] value() default {};
}
