package com.dugq.arithmetic.sort;

import com.dugq.arithmetic.util.MyArrayUtils;
import com.dugq.arithmetic.util.DoubleCounter;

import java.util.Arrays;

/**
 * Created by dugq on 2023/12/5.
 */
public class 冒泡排序 {

    public static void main(String[] args) {
        for (int i =0 ;i < 10; i++){
            int[] source = MyArrayUtils.randomIntArray(10,20);
            int[] array = Arrays.copyOf(source, source.length);
            bubbleSort(array).print("bubble sort foreach times = ","compare times = ");
            MyArrayUtils.validatorSortedArray(array, source,false);
        }
    }
    public static DoubleCounter bubbleSort(int[] array){
        DoubleCounter counter = new DoubleCounter(1);
        counter.start();
        for (int i =array.length;i>0;i--){
            for (int j=0; j < i-1; j++){
                counter.incrementFirst();
                if (array[j] > array[j+1]){
                    counter.incrementSecond();
                    MyArrayUtils.swapArray(array,j,j+1);
                }
            }
        }
        counter.end();
        return counter;
    }

}
