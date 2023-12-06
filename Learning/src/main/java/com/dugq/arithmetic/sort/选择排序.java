package com.dugq.arithmetic.sort;

import com.dugq.arithmetic.util.MyArrayUtils;
import com.dugq.arithmetic.util.DoubleCounter;

import java.util.Arrays;

/**
 * Created by dugq on 2023/12/5.
 */
public class 选择排序 {

    public static void main(String[] args) {
        for (int i =0 ;i < 10; i++){
            int[] source = MyArrayUtils.randomIntArray(10, 100);
            int[] array = Arrays.copyOf(source, source.length);
            selectSort(array).print("select sort foreach times = ","swap times = ");
            MyArrayUtils.validatorSortedArray(array,source,false);
        }
    }

    public static DoubleCounter selectSort(int[] array){
        DoubleCounter counter = new DoubleCounter(1);
        counter.start();
        for (int i = 0; i < array.length; i++){
            int min = i;
            for (int j = i+1; j<array.length; j++){
                counter.incrementFirst();
                if (array[min] > array[j]){
                    min = j;
                }
            }
            if (min!=i){
                MyArrayUtils.swapArray(array,i,min);
                counter.incrementSecond();
            }
        }
        counter.end();
        return counter;
    }

}
