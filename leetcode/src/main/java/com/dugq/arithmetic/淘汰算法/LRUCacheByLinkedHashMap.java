package com.dugq.arithmetic.淘汰算法;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by dugq on 2023/12/1.
 */
public class LRUCacheByLinkedHashMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 4;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private int capacity;
    private final LinkedHashMap<K, V> cache;

    public LRUCacheByLinkedHashMap(int capacity) {
        this.capacity = capacity;
        // 第三个参数true表示按照访问顺序排序，false表示插入顺序排序
        this.cache = new LinkedHashMap<K, V>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, true) {
            protected boolean removeEldestEntry(Map.Entry eldest) {
                // 当map元素数量大于指定的最大容量时，就自动删除最旧的数据
                return size() > LRUCacheByLinkedHashMap.this.capacity;
            }
        };
    }

    public synchronized V get(K key) {
        return cache.get(key);
    }

    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }
}
