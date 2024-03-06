package com.dugq.arithmetic.淘汰算法;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dugq on 2024/3/5.
 */
class LRUCache {

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        cache.put(2,1);
        cache.put(2,2);
        System.out.println(cache.get(2));
        cache.put(1,1);
        cache.put(4,1);
        System.out.println(cache.get(2));
    }

    Map<Integer,LRUNode> map = new HashMap<>();
    LRUNode head,tail;
    int capacity ;
    int size = 0;
    public LRUCache(int capacity) {
        this.capacity = capacity;
        head = tail = new LRUNode(0,0);
    }

    class LRUNode{
        int key;
        int val;
        LRUNode pre;
        LRUNode next;
        public LRUNode(int key,int val){
            this.key = key;
            this.val = val;
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
        if(node!=tail){
            // 从链表中移除
            node.pre.next = node.next;
            node.next.pre = node.pre;
            // 加入到tail后
            tail.next = node;
            node.pre = tail;
            node.next = null;
            // 移动tail指针
            tail = node;
        }
    }

    private void resize(){
        if(size<=capacity){
            return;
        }
        size--;
        LRUNode out = head.next;
        head.next = out.next;
        out.next.pre = head;
        map.remove(out.key);
    }

    public void put(int key, int value) {
        LRUNode old = map.get(key);
        if(old==null){
            LRUNode node = new LRUNode(key,value);
            map.put(key,node);
            size++;
            // 加入到tail后
            tail.next = node;
            node.pre = tail;
            // 移动tail指针
            tail = node;
            resize();
        }else {
            old.val = value;
            move2Tail(old);
        }
    }
}
