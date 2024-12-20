package com.dugq.arithmetic;

import org.junit.jupiter.api.Test;

import java.util.*;


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


    @Test
    public  void test123() {
        relocateMarbles(new int[]{2,45,45,48,51,57,67,73,78,78},
                new int[]{78,67,45,34,51,62,48,95,2,67},
                new int[]{34,65,62,95,62,12,85,67,79,71});
    }

    public List<Integer> relocateMarbles(int[] nums, int[] moveFrom, int[] moveTo) {
        Map<Integer,Integer> map = new HashMap<>(moveFrom.length);
        Map<Integer,List<Integer>> map2 = new HashMap<>(moveFrom.length);
        for(int i = 0 ; i < moveFrom.length; i++){
            int from = moveFrom[i];
            int to = moveTo[i];
            List<Integer> fromList = map2.getOrDefault(from,new LinkedList<>());
            if(!map.containsKey(from)){
                fromList.add(from);
            }
            map2.remove(from);
            for(Integer f : fromList){
                map.put(f,to);
            }
            if(map2.containsKey(to)){
                fromList.addAll(map2.get(to));
            }
            map2.put(to,fromList);
        }
        for(int i = 0 ; i<nums.length; i++){
            if(map.containsKey(nums[i])){
                nums[i] = map.get(nums[i]);
            }
        }
        Arrays.sort(nums);
        List<Integer> result = new LinkedList();
        for(int i = 0 ; i < nums.length; i++){
            if(i==0 || nums[i]!=nums[i-1]){
                result.add(nums[i]);
            }

        }
        return result;
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
