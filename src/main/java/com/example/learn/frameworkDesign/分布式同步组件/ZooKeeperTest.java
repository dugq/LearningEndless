package com.example.learn.frameworkDesign.分布式同步组件;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by dugq on 2023/8/28.
 */
public class ZooKeeperTest {

    @Test
    public void testZKClient() throws IOException {
        ZooKeeper zk = new ZooKeeper("",10000,null);
        zk.
    }
}
