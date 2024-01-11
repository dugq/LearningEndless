package com.dugq.frameworkDesign.seriallizing;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by dugq on 2023/12/8.
 */
public class ProtostuffSerialize implements MySerialize{

    private static final Cache<Class<?>, Schema<?>> workCahce = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS).maximumSize(1000).build();
    public <T> Schema<T> getSchema(Class<T> clazz, boolean useCache){
        if (!useCache){
            return RuntimeSchema.getSchema(clazz);
        }
        try {
            return (Schema<T>) workCahce.get(clazz,()-> RuntimeSchema.getSchema(clazz));
        } catch (ExecutionException e) {
            return RuntimeSchema.getSchema(clazz);
        }

    }


    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            return new byte[0];
        }
        Schema<Object> schema = (Schema<Object>)getSchema(obj.getClass(), true);
        return ProtobufIOUtil.toByteArray(obj, schema, LinkedBuffer.allocate());
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return deserialize(bytes,Object.class);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> cls) {
        if (Objects.isNull(bytes) || bytes.length==0){
            return null;
        }
        T message;
        try {
            message = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        ProtobufIOUtil.mergeFrom(bytes, message,getSchema(cls,true));
        return message;
    }
}
