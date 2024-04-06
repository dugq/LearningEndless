package com.dugq.arithmetic;

import com.dugq.arithmetic.util.DoubleCounter;

import java.util.Deque;
import java.util.LinkedList;

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

//        This.doubleCounter.printFist();
//        This.removeKdigits("1432219",3);
        This.splitArray(new int[]{7,2,5,10,8},2);
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


    public String removeKdigits(String num, int k) {
        int len = num.length();
        int j = 0;
        Deque<Character> stack = new LinkedList<>();
        for(int i = 0; i<k;i++){
            while (j<len) {
                char current = num.charAt(j);
                if(stack.size()==0){
                    stack.addLast(current);
                    j++;
                    continue;
                }
                char last = stack.peekLast();
                if(last>current){
                    stack.removeLast();
                    break;
                }
                stack.addLast(current);
                j++;
            }
            if(j==len){
                stack.removeLast();
            }
        }
        for(;j<len;j++){
            stack.addLast(num.charAt(j));
        }
        StringBuilder s = new StringBuilder();
        while(stack.size()>0){
            char current = stack.removeFirst();
            if(current=='0' && s.length()==0){
                continue;
            }
            s.append(current);
        }
        if(s.length()==0){
            return "0";
        }
        return s.toString();
    }

    public int splitArray(int[] nums, int k) {
        int len = nums.length;
        int total = 0;
        for(int i =0; i< len;i++){
            total+=nums[i];
        }
        int avg = total/k;
        return getMinSubTotal(nums,avg,k,0);
    }

    public int getMinSubTotal(int[] nums,int avg,int k,int start){
        int currentSum = 0;
        int end = nums.length-k;
        for(int i =start; i<=end;i++){
            currentSum+=nums[i];
            if(currentSum>avg && k>1){
                int max = Math.max(getMinSubTotal(nums, avg, k - 1, i + 1), currentSum);
                return Math.min(getMinSubTotal(nums,avg,k-1,i), max);
            }
        }
        if(k==1){
            return currentSum;
        }
        return getMinSubTotal(nums,avg,k-1,end+1);
    }


}
