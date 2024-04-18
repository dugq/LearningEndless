package com.dugq.agreement;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

/**
 * Created by dugq on 2024/4/16.
 */
public class ConnectAgreement extends DefaultFullHttpRequest {


    public ConnectAgreement(String uri) {
        super(HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
    }
}
