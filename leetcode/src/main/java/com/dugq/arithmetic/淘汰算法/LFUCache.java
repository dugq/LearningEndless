package com.dugq.arithmetic.淘汰算法;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dugq on 2024/3/5.
 * 结构如下：
 * LFUSlot head
 * LFUSlot slot1(count=1) head > LFUNode(key=1) > LFUNode(key=2) > tail
 * LFUSlot slot2(count=2) head > LFUNode(key=3) > LFUNode(key=4) > tail
 * LFUSlot tail
 * 类似二维表格
 * 一维链表count不同 head 小 tail 大
 * 二维中的链表count相同 head 先加入，tail 后加入
 *
 * 为什么是双向链表而不是单项？
 * 避免当更新count时，快速获取到前置节点，避免一次遍历
 *
 * 为什么要选择二维链表结构？
 * 如果选择 单个双向链表结构，则每次更新count时，移动node的复杂度 最差为 O(n)
 * 但是当采用二维时，二维中的head表示最久，tail表示最新
 */
public class LFUCache {

    public static void main(String[] args) {
        LFUCache cache = new LFUCache(2);
        cache.put(1,1);
        cache.print();
        cache.put(2,2);
        cache.print();
        System.out.println(cache.get(1)==1);
        cache.print();
        cache.put(3,3);
        cache.print();
        System.out.println(cache.get(2)==-1);
        cache.print();
        System.out.println(cache.get(3)==3);
        cache.print();
        cache.put(4,4);
        cache.print();
        System.out.println(cache.get(1)==-1);
        cache.print();
        System.out.println(cache.get(3)==3);
        cache.print();
        System.out.println(cache.get(4)==4);
        cache.print();
    }
    class LFUNode {
        int key;
        int val;
        LFUNode pre;
        LFUNode next;

        LFUSlot slot;

        int count;
        public LFUNode(int key,int val){
            this.key = key;
            this.val = val;
            this.count = 1;
        }
    }

    class LFUSlot {
        int count;
        LFUNode head;
        LFUNode tail;

        LFUSlot pre;
        LFUSlot next;

        public LFUSlot(int count){
            this.count = count;
            this.head = this.tail = new LFUNode(0,0);
        }

        public LFUSlot(LFUNode node){
            this.count = node.count;
            this.head = new LFUNode(0,0);
            this.tail = node;
            this.head.next = tail;
            this.tail.pre = this.head;

        }

        public void print() {
            System.out.print(count + ":  ");
            LFUNode index = head.next;
            while (index !=null) {
                System.out.print(index.key + " -> ");
                index = index.next;
            }
            System.out.println(" ");
        }
    }

    Map<Integer,LFUNode> map = new HashMap<>();

    LFUSlot head,tail;
    int capacity;
    int size;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.head = this.tail = new LFUSlot(0);
    }

    public int get(int key) {
        LFUNode node = map.get(key);
        if(node==null){
            return -1;
        }
        node.count++;
        moveNode(node);
        return node.val;
    }

    void moveNode(LFUNode node){
        // 从slot中删除node
        LFUSlot slot = removeNode(node);
        node.pre=null;
        node.next=null;
        addNode(slot.pre,node);
    }

    private LFUSlot removeNode(LFUNode node) {
        LFUSlot slot = node.slot;
        node.pre.next = node.next;
        if(node.next==null){
            slot.tail = node.pre;
        }else{
            node.next.pre = node.pre;
        }
        // slot废了
        if(slot.head==slot.tail){
            slot.pre.next = slot.next;
            if(slot.next==null) {
                tail = slot.pre;
            }else{
                slot.next.pre = slot.pre;
            }
        }
        return slot;
    }

    void addNode(LFUSlot index,LFUNode node){
        while(index.next!=null){
            LFUSlot next = index.next;
            if(next.count == node.count){
                node.slot = next;
                node.pre = next.tail;
                node.next = null;
                next.tail.next = node;
                next.tail = node;
                return;
            }
            if(next.count>node.count){
                break;
            }else{
                index = index.next;
            }
        }
        LFUSlot slot = new LFUSlot(node);
        node.slot = slot;
        slot.pre = index;
        slot.next = index.next;
        index.next = slot;
        if(slot.next==null){
            tail = slot;
        }else{
            slot.next.pre = slot;
        }
    }


    public void put(int key, int value) {
        LFUNode old = map.get(key);
        if(old==null){
            checkSize();
            LFUNode node = new LFUNode(key,value);
            map.put(key,node);
            addNode(head,node);
            size++;
        }else{
            old.val = value;
            old.count++;
            moveNode(old);
        }
    }

    void checkSize(){
        // 当容量满时，移除首个slot的首个node
        if(size==capacity){
            LFUSlot firstSlot =  head.next;
            LFUNode firstNode = firstSlot.head.next;
            map.remove(firstNode.key);
            removeNode(firstNode);
            size--;
        }
    }

    void print(){
        System.out.println("--------------------------------");
        LFUSlot index = head.next;
        while(index!=null){
            index.print();
            index = index.next;
        }
        System.out.println("--------------------------------");
    }
}
