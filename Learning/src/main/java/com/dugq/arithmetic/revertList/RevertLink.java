package com.dugq.arithmetic.revertList;

import com.dugq.arithmetic.LinkNode;

/**
 * Created by dugq on 2023/8/15.
 */
public class RevertLink {
    public static LinkNode reverseListByItor(LinkNode head) {
        // write code here
        LinkNode next;
        LinkNode pre = null;
        while((next = head.next)!=null){
            head.next = pre;
            pre = head;
            head = next;
        }
        head.next = pre;
        return head;
    }

    public static void main(String[] args) {
        LinkNode head = LinkNode.build(1,2,3);
//        head = reverseListByItor(head);
        head = reverseList(head);
       LinkNode.printNode(head);
    }

    public static LinkNode reverseList(LinkNode head){
        if (head == null || head.next == null){
            return head;
        }
        LinkNode next = head.next;
        LinkNode newHead = reverseList(head.next);
        next.next = head;
        head.next = null;
        return newHead;
    }
}
