package com.dugq.arithmetic.util;

/**
 * Created by dugq on 2023/8/15.
 */
public class ListNode {
    public int value;
    public ListNode next = null;

    public ListNode(){


    }
    public ListNode(int value) {
        this.value = value;
    }

    public ListNode(int val, ListNode next) { this.value = val; this.next = next; }

    public static ListNode build(int... val){
        ListNode next = null;
        ListNode head = null;
        for (int i =0; i<val.length; i++) {
            if (head==null){
                head = new ListNode(val[i]);
                next = head;
            }else{
                next = next.next = new ListNode(val[i]);
            }
        }
        return head;
    }

    public void print(){
        printNode(this);
    }

    public static void printNode(ListNode head){
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
