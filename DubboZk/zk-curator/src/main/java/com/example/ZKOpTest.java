package com.example;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.After;
import org.junit.Before;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dugq on 2023/9/7.
 */
@Slf4j
public class ZKOpTest {

    public static void main(String[] args) throws Exception {
        testRead();
        testAddAndDelete();
        testProtection();
        testAddACL();
        }

    private static void testRead() throws KeeperException, InterruptedException {
        ZooKeeper zk = ZkClientFactory.getZk();
        List<String> children = zk.getChildren("/", false);
        System.err.println(JSON.toJSONString(children));
        Stat stat = zk.setData("/dugq", "杜国庆".getBytes(), -1);
        System.out.println(JSON.toJSONString(stat));
    }


    public static void testAddACL() throws Exception {
        //客户端1 权限dugq
        ZooKeeper zk = ZkClientFactory.getZk();
        // 每个客户端都可以设置一个账户信息，密码不需要加密
        zk.addAuthInfo("digest","dugq:123".getBytes());
        //客户端2 权限dugq2
        CuratorFramework curatorClient = ZkClientFactory.getCuratorClient("dugq2:123");
        //zk.addAuthInfo("digest","dugq2:123".getBytes());
        List<ACL> list = new ArrayList<>();
        list.add(ZkClientFactory.getMyACL());
        list.add(ZkClientFactory.getMyACL2());
        // 赋予两个权限
        String s = zk.create("/dugq/acl/a", "acl test".getBytes(), list, CreateMode.EPHEMERAL);
        //权限1可读写
        Stat stat = zk.setData("/dugq/acl/a", "acl update".getBytes(), -1);

        try {
            //权限2只能读，不能写
            byte[] bytes = curatorClient.getData().forPath("/dugq/acl/a");
            curatorClient.setData().forPath("/dugq/acl/a", "acl 2".getBytes());
        }catch (Exception e){
            log.error("权限不够，不能写",e);
        }

        zk.create("/dugq/acl/b", "acl test".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
        zk.setData("/dugq/acl/b", "acl update".getBytes(), -1);
        try {
            curatorClient.setData().forPath("/dugq/acl/b", "acl 2".getBytes());
        }catch (Exception e){
            log.error("权限不够，不能写",e);
        }
        Thread.sleep(100000);
        System.out.println("s = "+s);
    }


    /**
     * 保护模式
     * 创建的Node生成一个随机的唯一的前缀，在每次创建之前，先检测一下是否已存在该前缀的节点。
     * 主要是解决有序节点，节点完整名称由zk服务端生成，如中间发生会话断开或其他情况，导致客户端未收到zk的回复消息，那该节点可能就成了无主节点
     */
    private static void testProtection() throws Exception {
        CuratorFramework curatorClient = ZkClientFactory.getCuratorClient();
        curatorClient.create().creatingParentsIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/dugq/base/sequential/lock-");
        curatorClient.create().creatingParentsIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/dugq/base/sequential/lock-");
        curatorClient.create().creatingParentsIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/dugq/base/sequential/lock-");
        curatorClient.create().creatingParentsIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/dugq/base/sequential/lock-");
        curatorClient.create().creatingParentsIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/dugq/base/sequential/lock-");
        printChildren(curatorClient,"/dugq/base/sequential","Protection-sequential ");
    }

    private static void testAddAndDelete() throws Exception {
        CuratorFramework curatorClient = ZkClientFactory.getCuratorClient();
        curatorClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/dugq/base/persistent1");
        curatorClient.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/dugq/base/persistent2");
        curatorClient.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/dugq/base/persistent2");
        printChildren(curatorClient,"/dugq/base","test");
        curatorClient.delete().deletingChildrenIfNeeded().forPath("/dugq/base");
    }

    public static void printChildren(CuratorFramework curatorClient, String parentPath, String flag){
        try {
            log.info("\n------------------------------------------------------------------------------\n"+
                    "\n------------------------------------------------------------------------------\n"+
                    "parent :{} \n children : {}-------\n falg :{} " +
                    "\n -------------------------------------------------------------------------------"
                    ,parentPath,curatorClient.getChildren().forPath(parentPath),flag);
        } catch (Exception e) {
            log.error("-------------print children error-----parent {}----flag {}----",parentPath,flag);
        }
    }

}
