package com.dugq.arithmetic.sort;

import com.alibaba.fastjson.JSON;
import com.dugq.arithmetic.ArrayNode;
import org.junit.Test;

/**
 * Created by dugq on 2023/8/15.
 */
public class 归并排序 {

    @Test
    public void test(){
        int[] array = ArrayNode.randomNoRepeatArray(1000);
        //节省了空间，但是merge的时候会编程n的2次方，时间复杂度会高一点
//        sortWithOutTemp(array,0,array.length-1);
        sortWithTemp(array,0,array.length-1,new int[array.length]);
        ArrayNode.validatorSort(array,array);
        System.out.println(JSON.toJSONString(array));

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
            // 当右子数组遍历完，将左子数组剩余的部分添加到temp
            if (j>right){
                temp[index++]=array[i++];
                continue;
            }
            // 左右子数组当前游标位相比较，较小的一方加入到temp，并且游标后移一位
            if (array[i] < array[j]){
                temp[index++] = array[i++];
            }else{
                temp[index++] = array[j++];
            }
        }
        //这里放弃将右子数组的剩余数据加入temp。因为右边本来就是靠后，如果其剩余说明其较大，那剩余的数组位置就不会发生变动

        //将temp的数组重新放回到原数组
        int t =0;
        while (t<index){
            array[left++] = temp[t++];
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
                ArrayNode.swap(array, mid, i);
                // 当左子序列的值较大移动到右子序列首部后，需要进行一次循环以保证右子序列依然有序
                for (int j = mid;j<right;j++){
                    if (array[j] > array[j+1]){
                        ArrayNode.swap(array, j, j+1);
                    }else{
                        break;
                    }
                }
            }
        }
    }


}
