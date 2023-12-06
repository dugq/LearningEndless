package com.dugq.arithmetic.revertList;

import com.dugq.arithmetic.util.LinkNode;
import org.junit.Test;

/**
 * Created by dugq on 2023/8/15.
 */
public class RevertByGroup {

    @Test
    public void test(){
        LinkNode list = LinkNode.build(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        LinkNode.printNode(revert(list,4));
    }

    public LinkNode revert(LinkNode head,int size){
        int index = 0;
        LinkNode next = null , pre=null;
        LinkNode tail = head;
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
