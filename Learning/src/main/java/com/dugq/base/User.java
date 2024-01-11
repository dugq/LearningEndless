package com.dugq.base;

import lombok.Data;

import java.util.Objects;

/**
 * Created by dugq on 2023/10/24.
 */
@Data
public class User {

    private String username;
    private String password;
    private int uid;

    public User() {
    }

    public User(int uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return uid == user.uid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }
}
