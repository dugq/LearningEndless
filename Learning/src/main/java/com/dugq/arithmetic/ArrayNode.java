package com.dugq.arithmetic;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dugq on 2023/8/15.
 */
public class ArrayNode {

  public static int[] randomArray(int size){
      int[] array = new int[size];
      for (int i = 0; i < size; i++) {
          array[i] = RandomUtils.nextInt(1,size*2);
      }
      return array;
  }
  public static int[] randomNoRepeatArray(int size){
        int[] array = new int[size];
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < size;) {
            int radom = RandomUtils.nextInt(1, size * 3);
            if (set.contains(radom)) {
                continue;
            }
            array[i++] = radom;
            set.add(radom);
        }
        return array;
  }

  public static boolean validatorSort(int[] array, int[] source){
      if (source.length!=array.length){
          throw new RuntimeException("长度错误");
      }
      int pre = array[0];
      for (int i =0 ;i < array.length; i++){
          if (pre>array[i]){
              System.out.println(JSON.toJSONString(array));
              throw new RuntimeException("第"+i+"个错误");
          }
          pre = array[i];
      }
      return true;
  }

    public static void swap(int[] array, int j, int i) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

}
