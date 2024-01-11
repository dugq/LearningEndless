package com.dugq.frameworkDesign.seriallizing;

/**
 * Created by dugq on 2023/12/8.
 */
public class FastSerialize implements MySerialize{
    @Override
    public byte[] serialize(Object obj) {
     return null;
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> cls) {
        return null;
    }
}
