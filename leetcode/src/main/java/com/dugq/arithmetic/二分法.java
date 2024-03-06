package com.dugq.arithmetic;

/**
 * Created by dugq on 2024/2/4.
 */
public class 二分法 {
    public static void main(String[] args) {
        二分法 This = new 二分法();
        // 搜索二维矩阵
//        This.searchMatrix(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 3);
        This.search(new int[]{1,2,3,0,0,0},2);
    }

    //给你一个满足下述两条属性的 m x n 整数矩阵：
    //每行中的整数从左到右按非严格递增顺序排列。
    //每行的第一个整数大于前一行的最后一个整数。
    //给你一个整数 target ，如果 target 在矩阵中，返回 true ；否则，返回 false 。
    public boolean searchMatrix(int[][] matrix, int target) {
        return false;
    }

    /**
     * 已知存在一个按非降序排列的整数数组 nums ，数组中的值不必互不相同。
     * 在传递给函数之前，nums 在预先未知的某个下标 k（0 <= k < nums.length）上进行了 旋转 ，
     * 使数组变为 [nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]]（下标 从 0 开始 计数）。
     * 例如， [0,1,2,4,4,4,5,6,6,7] 在下标 5 处经旋转后可能变为 [4,5,6,6,7,0,1,2,4,4] 。
     * 给你 旋转后 的数组 nums 和一个整数 target ，请你编写一个函数来判断给定的目标值是否存在于数组中。
     * 如果 nums 中存在这个目标值 target ，则返回 true ，否则返回 false 。
     */

    public boolean search(int[] nums, int target) {
        int left = 0;
        int right = nums.length -1;
        for(int i =1; i<nums.length;i++){
            if(nums[i]!=nums[0]){
                left = i-1;
                break;
            }
        }
        for(int i =right; i>=left;i--){
            if(nums[i]!=nums[0]){
                right = i;
                break;
            }
        }
        if(nums[left]==target || nums[right]==target){
            return true;
        }
        // 从前半部分找
        if(target > nums[0]){
            while(left+1<right){
                int middle = (left+right +1)/2;
                if(nums[middle]>target || nums[middle]<nums[0]){
                    right = middle;
                }else if(nums[middle]<target){
                    left = middle;
                }else{
                    return true;
                }
            }
            // 从后半部分找
        }else if(target < nums[0]){
            while(left+1<right){
                int middle = (left+right +1)/2;
                if(nums[middle]<target || nums[middle]>nums[0]){
                    left = middle;
                }else if(nums[middle]>target){
                    right = middle;
                }else{
                    return true;
                }
            }
        }else{
            return true;
        }
        return false;
    }
}
