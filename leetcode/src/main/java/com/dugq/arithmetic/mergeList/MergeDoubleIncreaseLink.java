package com.dugq.arithmetic.mergeList;

import com.dugq.arithmetic.util.DoubleCounter;
import com.dugq.arithmetic.util.ListNode;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

/**
 * Created by dugq on 2023/8/15.
 */
public class MergeDoubleIncreaseLink {

    @Test
    public void test(){
        ListNode.printNode(merge(ListNode.build(1, 3, 5), ListNode.build(2, 4, 6)));
        ListNode.printNode(merge(ListNode.build(1, 3, 5), ListNode.build(2, 3, 6)));
        ListNode.printNode(merge(ListNode.build(1, 3, 5,7,8,9), ListNode.build(2, 4, 6)));
        ListNode.printNode(merge(ListNode.build(1, 3, 5), ListNode.build(2, 4, 6,7,8)));

    }

    @Test
    public void mergeListArray(){
        mergeKLists(new ListNode[]{
                ListNode.build(1,4,5),
                ListNode.build(1,3,4),
                ListNode.build(2,6)
        }).print();
    }

    // 区间反转
    @Test
    public void reverseKGroupTest(){
        reverseKGroup(ListNode.build(1,2,3,4,5),3).print();
    }

    private ListNode merge(ListNode list1, ListNode list2) {
        if (list1==null){
            return list2;
        }
        if (list2==null){
            return list1;
        }
        ListNode head = null;
        ListNode tail = null;
        while (list1!=null && list2!=null){
            ListNode smaller ;
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

    public ListNode mergeKLists(ListNode[] lists) {
        ListNode head = new ListNode();
        ListNode tail = head;
        ListNode min = null;
        DoubleCounter counter = new DoubleCounter(1).start();
        while(true){
            for(int i =0 ; i<lists.length;i++){
                counter.incrementFirst();
                if(lists[i]==null){
                    continue;
                }
                if(min==null){
                    min = lists[i];
                    lists[i] = lists[i].next;
                    min.next = null;
                }else if(min.value>lists[i].value){
                    ListNode temp = lists[i];
                    lists[i] = lists[i].next;
                    temp.next = min;
                    min = temp;
                }
            }
            if(min==null){
                break;
            }
            tail.next = min;
            tail = tail.next;
            min = min.next;
        }
        counter.end().printFist();
        return head.next;
    }

    public ListNode reverseKGroup(ListNode head, int k) {
        head = new ListNode(0,head);
        int pos = 0;
        ListNode index = head;
        // 区间的前置节点，即：上一个区间的尾节点
        ListNode left = head;
        while(index!=null && index.next != null){
            index = index.next;
            if(++pos < k){
                continue;
            }
            // 反转 区间头部：left.next 区间尾节点 index
            ListNode subHead = left.next;
            // 把subHead的下一个节点拿出来插入到left后面
            while(--pos>0){
                // 1、先把subIndex.next记录下来
                ListNode temp = subHead.next;
                // 2、把subIndex踢掉
                subHead.next = temp.next;
                // 3、再把subIndex插入到left的后面
                temp.next = left.next;
                left.next = temp;
            }
            // index 重置为反转后尾节点，开始下一个区间遍历。 新的尾节点点就是之前的头节点
            left = index = subHead;
        }
        return head.next;
    }

    @Test
    public void testStringRepeat(){
        strStr("a","a");
        strStr2("mississippi","pi");
    }
    public int strStr(String haystack, String needle) {
        if(needle.length()==0 || haystack.length()==0 || haystack.length()<needle.length()){
            return -1;
        }
        for(int i =0; i<= haystack.length()-needle.length();i++){
            int j =0;
            int nextReapetStart = 0;
            for( ;j < needle.length(); j++){
                if (i+j>haystack.length()-1){
                    return -1;
                }
                if(nextReapetStart==0 && j>0 && haystack.charAt(i+j)==needle.charAt(0)){
                    nextReapetStart = i+j;
                }
                if(haystack.charAt(i+j)!=needle.charAt(j)){
                    if(nextReapetStart>0){
                        i = nextReapetStart;
                        j = 0;
                        nextReapetStart = 0;
                        continue;
                    }
                    break;
                }
                if(j==needle.length()-1){
                    return i;
                }
            }
        }
        return -1;
    }

    public int strStr2(String haystack, String needle) {

        if(needle.length()==0 || haystack.length()==0 || haystack.length()<needle.length()){
            return -1;
        }
        LinkedList<Integer> list = new LinkedList<>();
        for(int i =0; i< haystack.length();i++){
            char c = haystack.charAt(i);
            int max = list.size();
            for(int j =0; j<max;j++){
                int pos =list.removeLast();
                if(c!=needle.charAt(pos)){
                    continue;
                }
                if(pos==needle.length()-1){
                    return i-pos;
                }else{
                    list.addFirst(pos+1);
                }
            }
            if(c == needle.charAt(0)){
                if (needle.length()==1){
                    return i;
                }
                list.add(1);
            }
        }
        return -1;
    }


   @Test
    public void testDivide(){
       System.out.println(divide(-2147483648,-1));
   }

    // 加减法 实现 除法
    public int divide(int dividend, int divisor) {
        int result = 0;
        if(dividend==0){
            return 0;
        }
        int positive = 1;
        if(dividend>0){
            dividend=-dividend;
        }else{
            positive = -positive;
        }
        if(divisor>0){
            divisor = -divisor;
        }else{
            positive = -positive;
        }
        int divisorMulti = divisor;
        int divisorMultiNum = 1;
        while(dividend<=divisor){
            if(dividend<=divisorMulti){
                dividend-=divisorMulti;
                result-=divisorMultiNum;
                divisorMultiNum++;
                divisorMulti = divisorMulti+divisor;
            }else{
                divisorMulti = divisor;
                divisorMultiNum = 1;
            }
        }
        if(positive>0){
            if(result== -2147483648){
                return  2147483647;
            }
            return -result;
        }
        return result;
    }
}
