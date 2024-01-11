package com.dugq.arithmetic.revertList;

import com.dugq.arithmetic.util.ListNode;

/**
 * Created by dugq on 2023/8/15.
 */
public class RevertLink {
    public static ListNode reverseListByItor(ListNode head) {
        // write code here
        ListNode next;
        ListNode pre = null;
        while((next = head.next)!=null){
            head.next = pre;
            pre = head;
            head = next;
        }
        head.next = pre;
        return head;
    }

    public static void main(String[] args) {
        ListNode head = ListNode.build(1,2,3);
//        head = reverseListByItor(head);
        head = reverseList(head);
       ListNode.printNode(head);
    }

    public static ListNode reverseList(ListNode head){
        if (head == null || head.next == null){
            return head;
        }
        ListNode next = head.next;
        ListNode newHead = reverseList(head.next);
        next.next = head;
        head.next = null;
        return newHead;
    }
}
