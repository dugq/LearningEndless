package com.dugq.arithmetic.util;

/**
 * Created by dugq on 2023/8/15.
 */
public class LinkNode {
    public int value;
    public LinkNode next = null;
    public LinkNode(int value) {
        this.value = value;
    }

    public static LinkNode build(int... val){
        LinkNode next = null;
        LinkNode head = null;
        for (int i =0; i<val.length; i++) {
            if (head==null){
                head = new LinkNode(val[i]);
                next = head;
            }else{
                next = next.next = new LinkNode(val[i]);
            }
        }
        return head;
    }

    public static void printNode(LinkNode head){
        System.out.print("result:");
        while (head!=null){
            System.out.print(head.value);
            head = head.next;
            if (head!=null){
                System.out.print(",");
            }
        }
        System.out.println();
    }
}
