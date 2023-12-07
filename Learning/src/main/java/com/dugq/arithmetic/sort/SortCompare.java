package com.dugq.arithmetic.sort;

import com.dugq.arithmetic.util.MyArrayUtils;

import java.util.Arrays;

/**
 * Created by dugq on 2023/12/5.
 */
public class SortCompare {
    public static void main(String[] args) {
        int[] source = MyArrayUtils.randomIntArray(100000,2000000);
//        希尔排序.shellSort(Arrays.copyOf(source, source.length))
//                .print("shell rank foreach times = ","swap times = ");
//        快速插入排序.insertSort(Arrays.copyOf(source, source.length))
//                .print("insert rank foreach times = ","swap times = ");
//        冒泡排序.bubbleSort(Arrays.copyOf(source, source.length))
//                .print("bubble rank foreach times = ","swap times = ");
//        选择排序.selectSort(Arrays.copyOf(source, source.length)).
//                print("select rank foreach times = ","swap times = ");
        堆排序.heapSort(Arrays.copyOf(source, source.length))
                .print("heap rank foreach times = ","swap times = ");
        快速排序.fastSort(Arrays.copyOf(source, source.length))
                .print("fast rank foreach times = ","swap times = ");
        归并排序.mergeSort(Arrays.copyOf(source, source.length)).
                print("merge rank foreach times = ","copy times = ");
        
        TreeSort.treeSort(Arrays.copyOf(source, source.length)).
                print("tree rank foreach times = ","copy times = ");

        // 堆排序和归并排序最优，而快速排序次之，其他的都是垃圾
    }
}
