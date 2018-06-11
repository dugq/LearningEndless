package com.example.pojo.mbeans.MBeanImpl;

import com.example.pojo.mbeans.TestMBean;

/**
 * Created by dugq on 2018\6\10 0010.
 */
public class TestMBeanImpl implements TestMBean {
    @Override
    public double name() {
        return Math.random();
    }
}
