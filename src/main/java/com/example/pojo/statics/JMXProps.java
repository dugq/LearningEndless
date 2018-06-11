package com.example.pojo.statics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by dugq on 2018\6\10 0010.
 */
@ConfigurationProperties(prefix = "com.sun.management.jmxremote")
@Component
public class JMXProps {
    private String  port;
    private String authenticate;
    private String ssl;
    @Value("${java.rmi.server.hostname}")
    private String hostName;


    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setAuthenticate(String authenticate) {
        this.authenticate = authenticate;
    }

    public void setSsl(String ssl) {
        this.ssl = ssl;
    }

    public String getPort() {
        return port;
    }

    public String getAuthenticate() {
        return authenticate;
    }

    public String getSsl() {
        return ssl;
    }
}
