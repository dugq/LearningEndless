package com.example;

/**
 * Created by dugq on 2017/6/22.
 */
public enum Six {
    man(1), woman(0);

    private int value;

    Six(int value) {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }

    public String getDes(){
        return this.value == 0 ? "女" : "男";
    }

    @Override
    public String toString() {
        return getDes();
    }
}

