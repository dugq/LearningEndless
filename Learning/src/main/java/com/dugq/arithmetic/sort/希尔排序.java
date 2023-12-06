package com.dugq.arithmetic.sort;

import com.dugq.arithmetic.util.MyArrayUtils;
import com.dugq.arithmetic.util.DoubleCounter;

import java.util.Arrays;

/**
 * Created by dugq on 2023/12/5.
 */
public class 希尔排序 {

    public static void main(String[] args) {
        for (int i =0 ;i < 10; i++){
            insertSort();
        }
    }

    private static void insertSort() {
        int[] source = MyArrayUtils.randomIntArray(10,20);
        int[] array = Arrays.copyOf(source, source.length);
        shellSort(array);
        MyArrayUtils.validatorSortedArray(array, source,false);
    }

    public static DoubleCounter shellSort(int[] array) {
        DoubleCounter counter = new DoubleCounter(1);
        counter.start();
        for(int gap = array.length/2; gap >= 1 ; gap/=2){
           for (int i = 0; i < array.length-gap; i+=gap){
               for(int j = i+gap; j>0; j-=gap ){
                   counter.incrementFirst();
                   if(array[j-1] > array[j]){
                       counter.incrementSecond();
                       MyArrayUtils.swapArray(array,j-gap,j);
                   }else{
                      break;
                   }
               }
           }
        }
        counter.end();
        return counter;
    }

}
