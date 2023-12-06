package com.dugq.arithmetic.sort;

import com.dugq.arithmetic.util.MyArrayUtils;
import com.dugq.arithmetic.util.DoubleCounter;

import java.util.Arrays;

/**
 * Created by dugq on 2023/12/4.
 */
public class 快速插入排序 {

    public static void main(String[] args) {
        for (int i =0 ;i < 10; i++){
            insertSort();
        }
    }

    private static void insertSort() {
        int[] source = MyArrayUtils.randomIntArray(10,20);
        int[] array = Arrays.copyOf(source, source.length);
        insertSort(array);
        MyArrayUtils.validatorSortedArray(array, source,true);
    }

    public static DoubleCounter insertSort(int[] array) {
        DoubleCounter counter = new DoubleCounter(1);
        counter.start();
        for(int i = 0; i < array.length-1; i++){
            for(int j = i+1; j>0; j-- ){
                counter.incrementFirst();
                if(array[j-1] > array[j]){
                    counter.incrementSecond();
                    MyArrayUtils.swapArray(array,j-1,j);
                }else{
                    break;
                }
            }
        }
        counter.end();
        return counter;
    }
}
