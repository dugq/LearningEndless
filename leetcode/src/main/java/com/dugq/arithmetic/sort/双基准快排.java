package com.dugq.arithmetic.sort;

import com.dugq.arithmetic.util.DoubleCounter;

public class 双基准快排 {
    private DoubleCounter doubleCounter = new DoubleCounter(1);

    public int[] sort(int[] array){
        doubleCounter.start();
        sort(array,0,array.length);
        doubleCounter.print("双基准快排1","双基准快排2");
        return array;
    }

    public void sort(int[] array,int left,int right){
        if(array == null || left>=right || array.length==0 || left<0 || right >array.length ){
            return;
        }
        int pivot1 = left;
        int pivot2 = right-1;
        if(array[pivot1]>array[pivot2]){
            int temp = array[pivot1];
            array[pivot1] = array[pivot2];
            array[pivot2] = temp;
        }
        for(int i = pivot1+1; i<pivot2;){
            if(array[i]<array[pivot1]){
                int temp = array[i];
               array[i] = array[pivot1];
               array[pivot1] = temp;
            }else if(array[i]>array[pivot2]){
                int temp = array[i];
                for(int j = i; j<pivot2;j++){
                    array[j] = array[j+1];
                    doubleCounter.incrementSecond();
                }
                array[pivot2] = temp;
                pivot2--;
            }else{
                i++;
            }
        }
        sort(array,left,pivot1);
        sort(array,pivot1+1,pivot2);
        sort(array,pivot2+1,right);
    }


}
