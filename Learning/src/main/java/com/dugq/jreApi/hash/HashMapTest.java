package com.dugq.jreApi.hash;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dugq on 2024/7/3.
 */
public class HashMapTest {

    public static void main(String[] args) {
        testCapacityAndThreshold();
    }

    public static void testCapacityAndThreshold(){
        //threshold默认值16,capacity 未初始化
        Map<Integer,String> values = new HashMap<Integer,String>();
        values.size();
        // 插入第一条数据时初始化 table ，capacity
        values.put(0,"value0");

        values.size();
        for (int i = 1 ; i<12;i++){
            values.put(i*8,"value"+i);
        }
        // 插入12条数据后，触发扩容
        values.put(12*8,"12");

    }

}
