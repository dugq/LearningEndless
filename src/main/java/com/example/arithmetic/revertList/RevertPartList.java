package com.example.arithmetic.revertList;

import com.example.arithmetic.LinkNode;
import org.junit.Test;

/**
 * Created by dugq on 2023/8/15.
 */
public class RevertPartList {

    @Test
    public void test(){
        LinkNode list = LinkNode.build(1, 2, 3, 4, 5);
        doRevert(list,2,4);
        LinkNode.printNode(list);
    }

    private void doRevert(LinkNode head, int m , int n) {
        if (head == null || m >= n || m <=0){
            return;
        }
        int index = 0;
        LinkNode start = null;
        LinkNode pre = null;
        LinkNode partHead = null;
        LinkNode next = head;
        while (next!=null && index < n){
            index++;
            LinkNode indexNode = next;
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
