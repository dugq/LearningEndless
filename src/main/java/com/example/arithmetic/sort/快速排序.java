package com.example.arithmetic.sort;

import com.alibaba.fastjson.JSON;
import com.example.arithmetic.ArrayNode;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;

/**
 * Created by dugq on 2023/8/16.
 */
public class 快速排序 {

    @Test
    public void test(){
        int[] source = ArrayNode.randomNoRepeatArray(100);
        System.out.println("source : "+JSON.toJSONString(source));
        sort(source,0,source.length-1);
        System.out.println("sorted : "+JSON.toJSONString(source));
        ArrayNode.validatorSort(source,source);
    }

    private void sort(int[] source, int left, int right) {
        if (right<=left){
            return;
        }
       int pivot = getPivot(source,left,right);
       sort(source,left,pivot-1);
       sort(source,pivot+1,right);
    }

    // 快排的核心： 将数组按照基准划分为 左小右大
    private int getPivot(int[] source, int left, int right) {
        // pivot 初始化随机选举
        int pivot = right;
        int location = left;
        for (int i = left; i < right; i++) {
            if (source[i] <= source[pivot]){
                ArrayNode.swap(source,location,i);
                location ++;
            }
        }
        ArrayNode.swap(source,location,pivot);
        return location;
    }

}
