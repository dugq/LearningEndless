package com.dugq.arithmetic.淘汰算法;

import com.dugq.arithmetic.util.DoubleCounter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by dugq on 2024/3/29.
 */
public class LFUCacheByHeapArray {
    DoubleCounter counter = new DoubleCounter(1);
    Map<Integer,Entry> map = new HashMap<>();
    // 为了方便删除，或者替换使用次数最小的元素
    // 使用次数最小的放在前面
    Entry[] entries;
    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *
     * lfu design
     * @param operators int整型二维数组 ops
     * @param k int整型 the k
     * @return int整型一维数组
     */
    public int[] LFU (int[][] operators, int k) {
        // write code here
        entries = new Entry[k];
        int opSize = operators.length;
        List<Integer> result = new LinkedList<>();
        long start = System.currentTimeMillis();
        for(int i = 0; i<opSize;i++){
            int[] op = operators[i];
            if(op[0] == 1){
                set(op[1],op[2]);
            }else if(op[0] == 2){
                result.add(get(op[1]));
            }
            System.out.println("coust = "+ (System.currentTimeMillis()-start));
        }
        int[] a = new int[result.size()];
        for(int i = 0; i<result.size();i++){
            a[i] = result.get(i);
        }
        return a;
    }

    public LFUCacheByHeapArray(int capacity){
        this.entries = new Entry[capacity];
    }

    public void set(int key,int value){
        if(map.containsKey(key)){
            map.get(key).set(value);
        }else if(map.size()==entries.length){
            map.remove(entries[0].key);
            entries[0] = new Entry(key,value,0);
            adjust(entries[0]);
        }else{
            Entry entry =  new Entry(key,value,0);
            map.put(key,entry);
            build(entry);
        }
    }

    public int get(int key){
        Entry entry = map.get(key);
        if(entry == null){
            return -1;
        }
        return entry.get();
    }

    private int parent(int index){
        return (index-1) / 2;
    }

    private int leftChild(int index){
        return index*2+1;
    }

    // 追加元素
    private void build(Entry entry){
        entries[map.size()-1] = entry;
        int index = map.size()-1;
        while(index>0){
            counter.incrementFirst();
            int parent = parent(index);
            if(entries[parent].compare( entries[index])>0){
                swap(index,parent);
            }
            index = parent;
        }
    }

    private void swap(int x,int y){
        Entry temp = entries[x];
        entries[x] = entries[y];
        entries[y] = temp;
        entries[x].pos = x;
        entries[y].pos = y;
    }

    // entry的tiems增加了，或者entry是新元素，注意：这里只会向下调整，新元素只能替换root节点
    private void adjust(Entry entry){
        int index = entry.pos;
        int leftIndex = leftChild(index);
        while(leftIndex<map.size()){
            counter.incrementFirst();
            int minIndex = leftIndex;
            // 优先选择左侧，其次选择右侧，相等也是选择左侧。定义左侧比右侧先set/get
            if(leftIndex+1 < map.size()
                    && entries[leftIndex].compare(entries[leftIndex+1]) > 0){
                minIndex = leftIndex+1;
            }
            // 相等也交换，让相等的情况下，后修改的跑到最后取
            if(entries[index].compare(entries[minIndex])<0){
                return;
            }
            swap(minIndex,index);
            index = minIndex;
            leftIndex = leftChild(index);
        }
    }



    class Entry{
        int key;
        int value;
        int times;
        long timeStamp;
        int pos;

        public int compare(Entry entry){
            if(this.times != entry.times){
                return this.times - entry.times;
            }
            return (int)(this.timeStamp - entry.timeStamp);
        }

        public Entry(int key,int val,int pos){
            this.key = key;
            this.value = val;
            this.times = 1;
            this.pos = pos;
            this.timeStamp = System.currentTimeMillis();
        }

        public void set(int val){
            this.value = val;
            this.times++;
            this.timeStamp = System.currentTimeMillis();
            adjust(this);
        }

        public int get(){
            this.times++;
            this.timeStamp = System.currentTimeMillis();
            adjust(this);
            return this.value;
        }
    }
}
