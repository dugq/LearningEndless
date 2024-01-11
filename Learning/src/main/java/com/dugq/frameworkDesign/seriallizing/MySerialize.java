package com.dugq.frameworkDesign.seriallizing;

/**
 * Created by dugq on 2023/12/8.
 */
public interface MySerialize {

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes);

    <T> T deserialize(byte[] bytes,Class<T> cls);

}
