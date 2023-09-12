package com.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by dugq on 2023/9/6.
 */
@Slf4j
public class ZkClientFactory {
    private static final String ZK_SERVER_URL = "121.36.211.210:2181";
    private static ZooKeeper zooKeeper;
    private static CuratorFramework curatorFramework;

    public static ZooKeeper getZk(Watcher watcher){
        if (Objects.nonNull(zooKeeper)){
            return zooKeeper;
        }
        try {
            return zooKeeper = new ZooKeeper(ZK_SERVER_URL,1000,watcher);
        } catch (IOException e) {
            log.error("zk连接错误",e);
            throw new RuntimeException(e);
        }
    }

    public static ZooKeeper getZk(){
        return getZk(event -> {
            Watcher.Event.EventType type = event.getType();
            log.error("事件发生:{}",type.name());
        });
    }

    public static ACL getMyACL(){
        Id m = new Id();
        m.setScheme("digest");
        try {
            m.setId(DigestAuthenticationProvider.generateDigest("dugq:123"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
        return  new ACL(ZooDefs.Perms.ALL,m);
    }

    public static ACL getMyACL2(){
        Id m = new Id();
        m.setScheme("digest");
        try {
            m.setId(DigestAuthenticationProvider.generateDigest("dugq2:123"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
        return  new ACL(ZooDefs.Perms.READ,m);
    }

    public static ArrayList<ACL> getALL(){
        return ZooDefs.Ids.OPEN_ACL_UNSAFE;
    }
    public static CuratorFramework getCuratorClient(){
        return getCuratorClient(null);
    }

    public static CuratorFramework getCuratorClient(String ipPwd){
        if (Objects.nonNull(curatorFramework)){
            return curatorFramework;
        }
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().connectString(ZK_SERVER_URL)
                .sessionTimeoutMs(80000)
                .connectionTimeoutMs(5000)
                .retryPolicy(new RetryOneTime(1000));
        if (StringUtils.isNotBlank(ipPwd)){
            builder.authorization("digest",ipPwd.getBytes());
        }
        curatorFramework = builder.build();
        curatorFramework.start();
        try {
            boolean connected = curatorFramework.blockUntilConnected(10, TimeUnit.SECONDS);
            if (!connected){
                throw new RuntimeException("zk connection timeout");
            }
        } catch (InterruptedException e) {
            log.error("zk connect error",e);
            return null;
        }
        return curatorFramework;
    }

}
