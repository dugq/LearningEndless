package com.dugq.arithmetic.sort;

import com.dugq.arithmetic.util.DoubleCounter;
import com.dugq.arithmetic.util.MyArrayUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by dugq on 2023/8/16.
 */
public class 快速排序 {
    private DoubleCounter doubleCounter = new DoubleCounter(1);

    @Test
    public void test(){
        int[] source = MyArrayUtils.randomIntArray(10,100);
        int[] array = Arrays.copyOf(source, source.length);
        fastSort(array).print("fast sort foreach times = ","swap times = ");
        MyArrayUtils.validatorSortedArray(array,source,false);
    }

    @Test
    public void testForeachVersion(){
        int[] source = MyArrayUtils.randomIntArray(10,100);
        int[] array = Arrays.copyOf(source, source.length);
        sortWithArray(array).print("fast sort foreach times = ","swap times = ");
        MyArrayUtils.validatorSortedArray(array,source,false);
    }

    public static DoubleCounter fastSort(int[] array){
        快速排序 test = new 快速排序();
        test.doubleCounter.start();
        test.sortWithArray(array);
        test.doubleCounter.end();
        return test.doubleCounter;
    }

    private void sort(int[] source, int left, int right) {
        if (right<=left){
            return;
        }
       int pivot = getPivot(source,left,right);
        sort(source,left,pivot-1);
       sort(source,pivot+1,right);


    }

    private class DoublePivot{
        int left;
        int right;

        public DoublePivot(int left, int right) {
            this.left = left;
            this.right = right;
        }
    }

    private DoubleCounter sortWithArray(int[] source){
        LinkedList<DoublePivot> pivots = new LinkedList<>();
        pivots.add(new DoublePivot(0,source.length-1));
        while (pivots.size()>0){
            DoublePivot next = pivots.poll();
            int pivot = getPivot(source,next.left,next.right);
            if (pivot+1<next.right){
                pivots.push(new DoublePivot(pivot+1,next.right));
            }
            if (pivot-1>next.left){
                pivots.push(new DoublePivot(next.left,pivot-1));
            }
        }
        return doubleCounter;
    }

    // 快排的核心： 将数组按照基准划分为 左小右大
    private int getPivot(int[] source, int left, int right) {
        // pivot 初始化随机选举
        int pivot = right;
        int location = left;
        for (int i = left; i < right; i++) {
            doubleCounter.incrementFirst();
            if (source[i] <= source[pivot]){
                MyArrayUtils.swapArray(source,location,i);
                doubleCounter.incrementSecond();
                location ++;
            }
        }
        MyArrayUtils.swapArray(source,location,pivot);
        doubleCounter.incrementSecond();
        return location;
    }

}
