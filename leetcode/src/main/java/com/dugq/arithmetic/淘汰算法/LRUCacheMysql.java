package com.dugq.arithmetic.淘汰算法;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dugq on 2024/3/5.
 *
 * mysql LRU：
 *  head 表示最近使用
 *
 *  oldHead 表示最近加入
 *
 *  tail 表示最近加入最久加入
 *
 *  put： mysql 一般都是get(key,loader)形式，所以实际不存在单独的put操作
 *  1、如果区域满了，踢出tail
 *  2、新节点加入到oldHead前，并替换oldHead
 *  3、如果是更新，则延用get操作 （这里自定义的，mysql不存在这个）
 *
 *  get:
 *  1、检查距离节点put的时间，大于1s，更新到head
 *  2、否则更新到oldHead
 *
 */
class LRUCacheMysql {

    public static void main(String[] args) {
        LRUCacheMysql cache = new LRUCacheMysql(2);
        cache.put(2,1);
        cache.put(2,2);
        System.out.println(cache.get(2));
        cache.put(1,1);
        cache.put(4,1);
        System.out.println(cache.get(2));
    }

    Map<Integer,LRUNode> map = new HashMap<>();
    LRUNode head,tail;

    LRUNode oldHead;
    int capacity ;
    int size = 0;

    int oldSize;

    int oldCapacity;
    public LRUCacheMysql(int capacity) {
        this.capacity = capacity;
        head = tail = new LRUNode(0,0);
        oldCapacity = capacity * 6 / 10;
    }

    class LRUNode{
        int key;
        int val;
        LRUNode pre;
        LRUNode next;

        long addTime;
        public LRUNode(int key,int val){
            this.key = key;
            this.val = val;
            addTime = System.currentTimeMillis();
        }
    }

    public int get(int key) {
        LRUNode result = map.get(key);
        if(result==null){
            return -1;
        }
        move2Tail(result);
        return result.val;
    }

    private void move2Tail(LRUNode node){
        // 移动到head
        if(System.currentTimeMillis()-node.addTime>1000 && node!=head.next){
           if(node==oldHead){
               oldHead = oldHead.next;
           }
           // 删除node
           node.pre.next = node.next;
           if(node.next!=null){
               node.next.pre = node.pre;
           }else{
               tail = node.pre;
           }
           // 添加到head后面
           node.pre = head;
           node.next = head.next;
           head.next = node;
           if(node.next!=null){
               node.next.pre = node;
           }else{
               tail = node;
           }

        // 移动到oldHead
        }else if(System.currentTimeMillis()-node.addTime<=1000 && oldHead!=node){
            // 删除
            if (node==tail){
                node.pre.next = null;
                tail = tail.pre;
            }else{
                node.pre.next = node.next;
                node.next.pre = node.pre;
            }
            // 追加
            node.pre =  oldHead.pre;
            node.next = oldHead;
            node.pre.next = node;
            node.next.pre = node;
            oldHead = node;
        }
    }

    private void resize(){
        if(size<capacity){
            return;
        }
        size--;
        map.remove(tail.key);
        tail = tail.pre;
        tail.next = null;
    }

    public void put(int key, int value) {
        LRUNode old = map.get(key);
        if(old==null){
            resize();
            LRUNode node = new LRUNode(key,value);
            map.put(key,node);
            size++;
            // 追加到尾部，并标记oldHead区域
            if(oldHead == null){
                tail.next = node;
                node.pre = tail;
                tail = oldHead = node;
            // 追加到oldHead前，并替换oldHead
            }else{
                node.next = oldHead;
                node.pre = oldHead.pre;
                node.pre.next = node;
                node.next.pre = node;
                oldHead = node;
            }
        }else {
            old.val = value;
            move2Tail(old);
        }
    }
}
