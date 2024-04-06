package com.dugq.frameworkDesign.seriallizing;

/**
 * Created by dugq on 2024/4/7.
 */
public class SerializationException extends RuntimeException {
    public SerializationException(String message){
        super(message);
    }

    public SerializationException(String message,Exception e){
        super(message,e);
    }
}
