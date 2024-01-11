package com.dugq.arithmetic.revertList;

import com.dugq.arithmetic.util.ListNode;
import org.junit.Test;

/**
 * Created by dugq on 2023/8/15.
 */
public class RevertByGroup {

    @Test
    public void test(){
        ListNode list = ListNode.build(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ListNode.printNode(revert(list,4));
    }

    public ListNode revert(ListNode head, int size){
        int index = 0;
        ListNode next = null , pre=null;
        ListNode tail = head;
        while (head != null && ++index <= size){
            next = head.next;
            head.next = pre;
            pre = head;
            head = next;
        }
        if (head!=null){
            tail.next =  revert(head,size);
        }
        return pre;
    }

}
