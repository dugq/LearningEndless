package com.dugq.jdbc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by dugq on 2024/1/16.
 */
@Setter
@Getter
@ToString
public class User {
    private long id;
    private String name;
    private int age;
}
