package com.dugq.arithmetic;

import com.dugq.arithmetic.util.DoubleCounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dugq on 2024/2/22.
 */
public class 贪心算法 {
    DoubleCounter doubleCounter = new DoubleCounter(1);
    public static void main(String[] args) {
        贪心算法 This = new 贪心算法();
//        System.out.println(This.jump(new int[]{2,3,0,1,4}));
//        System.out.println(This.jump(new int[]{1,1,1,1}));

//        System.out.println(This.numDistinct("rabbbit","rabbit"));

        This.doubleCounter.printFist();
    }

    public int jump(int[] nums) {
        if(nums.length==1){
            return 0;
        }
        // 贪心算法，在每一跳的范围内查找能跳到的最远点，以该点为落脚点，向下个范围跳跃
        // step 记录步数 stop 记录每跳的结束位点
        int step =1;
        int stop = nums[0];
        if (stop>=nums.length-1){
            return step;
        }
        int index = 1;
        int nextStop = 0;
        // 只要范围结束点在nums.length-1 之前，那么就需要继续找。 大概率是不会找到最后一点点的
        while(nextStop < nums.length-1 && index < nums.length){
            if(index>stop){
                step++;
                stop = nextStop;
            }
            int max = nums[index]+index;
            if(max>nextStop){
                nextStop = max;
            }
            if (nextStop>=nums.length-1){
                step++;
                break;
            }
            index++;
        }
        return step;
    }


}
