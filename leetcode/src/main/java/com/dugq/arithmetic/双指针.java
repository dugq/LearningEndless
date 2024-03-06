package com.dugq.arithmetic;

import com.dugq.arithmetic.util.ListNode;

/**
 * Created by dugq on 2024/2/4.
 */
public class 双指针 {

    public static void main(String[] args) {
        双指针 This = new 双指针();
        // 颜色分类
        // This.sortColors(new int[]{2, 0, 2, 1, 1, 0});


    }

    //给定一个包含红色、白色和蓝色、共 n 个元素的数组 nums ，原地对它们进行排序，使得相同颜色的元素相邻，并按照红色、白色、蓝色顺序排列。
    //我们使用整数 0、 1 和 2 分别表示红色、白色和蓝色。
    //必须在不使用库内置的 sort 函数的情况下解决这个问题。
    public void sortColors(int[] nums) {
        int whiteStart = 0; // 包含
        int whiteEnd = 0; // 不包含
        int white = 1;
        int red = 0;
        for(int i =0;i<nums.length; i++){
            if(nums[i]==white){
                if(i>whiteEnd){
                    nums[i] = nums[whiteEnd];
                    nums[whiteEnd] = white;
                }
                whiteEnd ++;
            }else if(nums[i]<white){
                nums[i] = nums[whiteStart];
                nums[whiteStart] = red;
                if(whiteEnd>whiteStart){
                    nums[i] = nums[whiteEnd];
                    nums[whiteEnd] = white;
                }
                whiteStart++;
                whiteEnd++;
            }
        }
    }

    // 判断链表中是否存在环 双指针之快慢指针 https://leetcode.cn/problems/linked-list-cycle/
    public boolean hasCycle(ListNode head) {
        // 修改next指针为特殊标记，这样只需遍历n+1次，head 必然等于特殊标记了
        // 优点 一次遍历就可以解决
        // 缺点 修改了入参，导致本方法副作用极大
        // ListNode node = new ListNode(0);
        // while(head!=null){
        //     if(head == node){
        //         return true;
        //     }
        //     ListNode next = head.next;
        //     head.next = node;
        //     head = next;
        // }
        // return false;

        // 双指针 之 快慢指针
        // 快指针每次后移n=2步，慢指针每次向后移动m=1步 （n>m）
        // 时间复杂度 = n + 环的个数x
        ListNode fast = head;
        ListNode slow = head;
        while(fast!=null){
            fast = fast.next;
            if(fast!=null){
                fast = fast.next;
            }
            if(fast==slow){
                return true;
            }
            slow = slow.next;
        }
        return false;
    }

}
