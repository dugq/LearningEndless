package com.dugq.arithmetic.mergeList;

import com.dugq.arithmetic.LinkNode;
import org.junit.jupiter.api.Test;

/**
 * Created by dugq on 2023/8/15.
 */
public class MergeDoubleIncreaseLink {

    @Test
    public void test(){
        LinkNode.printNode(merge(LinkNode.build(1, 3, 5),LinkNode.build(2, 4, 6)));
        LinkNode.printNode(merge(LinkNode.build(1, 3, 5),LinkNode.build(2, 3, 6)));
        LinkNode.printNode(merge(LinkNode.build(1, 3, 5,7,8,9),LinkNode.build(2, 4, 6)));
        LinkNode.printNode(merge(LinkNode.build(1, 3, 5),LinkNode.build(2, 4, 6,7,8)));

    }

    private LinkNode merge(LinkNode list1, LinkNode list2) {
        if (list1==null){
            return list2;
        }
        if (list2==null){
            return list1;
        }
        LinkNode head = null;
        LinkNode tail = null;
        while (list1!=null && list2!=null){
            LinkNode smaller ;
            if (list1.value <= list2.value){
                smaller = list1;
                list1 = list1.next;
            } else{
                smaller = list2;
                list2 = list2.next;
            }
            if (head == null){
                tail = head = smaller;
            }else{
                tail.next = smaller;
                tail = smaller;
            }
        }
        if (list1!=null){
            tail.next = list1;
        }
        if (list2!=null){
            tail.next = list2;
        }
        return head;
    }

}
