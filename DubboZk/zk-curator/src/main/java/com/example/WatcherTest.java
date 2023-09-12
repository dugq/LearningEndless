package com.example;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dugq on 2023/9/6.
 */
public class WatcherTest {

    public static void main(String[] args) throws InterruptedException, KeeperException {
        Stat stat = new Stat();
        byte[] data = ZkClientFactory.getZk().getData("/dugq", event -> {
            System.out.println(event.getType());
        }, stat);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();

    }

}
