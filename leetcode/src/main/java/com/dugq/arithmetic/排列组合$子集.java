package com.dugq.arithmetic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dugq on 2024/2/4.
 */
public class 排列组合$子集 {
    public static void main(String[] args) {
        排列组合$子集 This = new 排列组合$子集();
        // 最基本的，罗列所有可能的组合

        System.out.println(This.subsets(new int[]{1,2,3}));
    }


    /**
     * 给你一个整数数组 nums ，数组中的元素 互不相同 。
     * 返回该数组所有可能的子集（幂集）。
     * 解集 不能 包含重复的子集。你可以按 任意顺序 返回解集。
     */

    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new LinkedList<>();
        result.add(new ArrayList<>());
        doCombine(result,nums,0,new LinkedList<>());
        return result;
    }

    public void doCombine(List<List<Integer>> result, int[] nums, int start, LinkedList<Integer> added){
        for(int i =start;i<nums.length;i++){
            added.addLast(nums[i]);
            result.add(new LinkedList(added));
            doCombine(result,nums,i+1,added);
            added.removeLast();
        }
    }
}
