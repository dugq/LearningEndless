package com.dugq.arithmetic.revertList;

import com.dugq.arithmetic.util.ListNode;
import org.junit.Test;

/**
 * Created by dugq on 2023/8/15.
 */
public class RevertPartList {

    @Test
    public void test(){
        ListNode list = ListNode.build(1, 2, 3, 4, 5);
        doRevert(list,2,4);
        ListNode.printNode(list);
    }

    private void doRevert(ListNode head, int m , int n) {
        if (head == null || m >= n || m <=0){
            return;
        }
        int index = 0;
        ListNode start = null;
        ListNode pre = null;
        ListNode partHead = null;
        ListNode next = head;
        while (next!=null && index < n){
            index++;
            ListNode indexNode = next;
            next = next.next;
            if (index==m-1){
               start = indexNode;
               partHead = indexNode.next;
            }
            if (index>=m && index<= n){
                indexNode.next = pre;
                pre = indexNode;
            }
            if (index==n){
                start.next = indexNode;
                partHead.next = next;
            }
        }
    }


}
