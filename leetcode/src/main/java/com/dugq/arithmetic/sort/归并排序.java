package com.dugq.arithmetic.sort;

import com.dugq.arithmetic.util.DoubleCounter;
import com.dugq.arithmetic.util.MyArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Created by dugq on 2023/8/15.
 */
public class 归并排序 {
    private DoubleCounter counter = new DoubleCounter(1);

    @Test
    public void test(){
        int[] source = MyArrayUtils.randomIntArray(1000,2000);
        int[] array = Arrays.copyOf(source, source.length);
        //节省了空间，但是merge的时候会编程n的2次方，时间复杂度会高一点
//        sortWithOutTemp(array,0,array.length-1);
        sortWithTemp(array,0,array.length-1,new int[array.length]);
        MyArrayUtils.validatorSortedArray(array,source);

    }

    public static DoubleCounter mergeSort(int[] array){
        return new 归并排序().sort(array);
    }

    private DoubleCounter sort(int[] array){
        counter.start();
        sortWithTemp(array,0,array.length-1,new int[array.length]);
        counter.end();
        return this.counter;
    }

    private void sortWithTemp(int[] array, int left, int right, int[] temp) {
        if (left<right){
            int mid = (left+right)/2;
            sortWithTemp(array,left,mid,temp);
            sortWithTemp(array,mid+1,right,temp);
            mergeArrayByTemp(array,left,mid+1,right,temp);
        }
    }

    private void mergeArrayByTemp(int[] array, int left, int mid, int right, int[] temp) {
        int index = 0;
        int i = left , j = mid;
        while (i < mid){
            counter.incrementFirst();
            // 当右子数组遍历完，将左子数组剩余的部分添加到temp
            if (j>right){
                temp[index++]=array[i++];
                counter.incrementSecond();
                continue;
            }
            // 左右子数组当前游标位相比较，较小的一方加入到temp，并且游标后移一位
            if (array[i] < array[j]){
                temp[index++] = array[i++];
                counter.incrementSecond();
            }else{
                temp[index++] = array[j++];
                counter.incrementSecond();
            }
        }
        //这里放弃将右子数组的剩余数据加入temp。因为右边本来就是靠后，如果其剩余说明其较大，那剩余的数组位置就不会发生变动

        //将temp的数组重新放回到原数组
        int t =0;
        while (t<index){
            array[left++] = temp[t++];
            counter.incrementSecond();
        }
    }

    private void sortWithOutTemp(int[] array, int left, int right) {
        if (left<right){
            int mid = (left+right)/2;
            sortWithOutTemp(array,left,mid);
            sortWithOutTemp(array,mid+1,right);
            mergeArrayOutTemp(array,left,mid+1,right);
        }
    }

    // 这样的方案虽然减少了temp临时变量，但因为嵌套的缘故，时间复杂度要高很多
    private void mergeArrayOutTemp(int[] array, int left, int mid, int right) {
        for (int i = left; i<mid; i++){
            // 主循环把右子序列较小的移动的左序列中
            if (array[i] > array[mid]){
                MyArrayUtils.swapArray(array, mid, i);
                // 当左子序列的值较大移动到右子序列首部后，需要进行一次循环以保证右子序列依然有序
                for (int j = mid;j<right;j++){
                    if (array[j] > array[j+1]){
                        MyArrayUtils.swapArray(array, j, j+1);
                    }else{
                        break;
                    }
                }
            }
        }
    }


}
