package com.dugq.arithmetic;

import org.junit.jupiter.api.Test;


/**
 * Created by dugq on 2023/8/15.
 */
public class MovingArray {

    private Integer[] array = new Integer[]{1,2,3,4,5,6};

    /**
     * 把数组的后n位移动到前面
     * 例：[6,2] 输入 1，2，3，4，5，6 -> 5,6,1,2,3,4
     */
    @Test
    public void test(){
        doMoving(array,2);
        printArray(array);
    }

    private void printArray(Integer[] array){
        System.out.println("result = ");
        for (int i =0; i<array.length; i++){
            if (i>0){
                System.out.print(",");
            }
            System.out.print(array[i]);
        }
        System.out.println();
    }

    private void doMoving(Integer[] array,int moving){
        int index = array.length - moving;
        for(int i = index; i<array.length;i++){
            for (int j =index;j > 0; j--) {
                int temp = array[j];
                array[j] = array[j -1];
                array[j-1] = temp;
            }
        }
    }



}
