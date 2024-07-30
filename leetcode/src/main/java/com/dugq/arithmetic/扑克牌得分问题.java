package com.dugq.arithmetic;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 给定一组扑克牌，总共有5张。每张牌有四种花色，分别是红桃、黑桃、红方片、黑麻花。每张牌可取值在【A，1，2，3，4，5，6，7，8，9，10，J，Q，K】范围内。现在给定一组判定逻辑，求出其最大牌型。
 * <li>牌型一：五张牌连续出现，且花色相同，则称其为<font color="red"><strong>同花顺</strong></font>。注意，包含A时，只允许A，1，2，3，4和10，J，Q，K，A这两种同花顺组合，不允许出现类似于Q，K，A，1，2。</li><li>牌型二：如果出现四张一样大小的牌，则称其为<font color="red"><strong>四元</strong></font>。</li><li>牌型三：如果五张牌的花色一样，则称其为<font color="red"><strong>同花</strong></font>。</li><li>牌型四：如果出现五张连续的牌，则称其为<font color="red"><strong>顺子</strong></font>【tip: 要求五张牌花色不能相同，否则为<strong>同花顺</strong>】。</li><li>牌型五：如果出现三张相同大小的牌和另外两张相同的牌，则称其为<font color="red"><strong>三元二目</strong></font>。</li><li>牌型六：如果出现三张相同大小的牌和另外两张不同的牌，则称其为<font color="red"><strong>三元</strong></font>。</li><li>牌型七：其它组合。</li>
 *
 * <p>现在用V表示红桃，B表示黑桃，N表示方片，M表示麻花。现在输入5行字符串，每行字符串包含两个字字符串，之间用空格隔开
 */
public class 扑克牌得分问题 {

    public static void main(String[] args) {
        Map<String,Integer> map = new HashMap<>();
        map.put("J",11);
        map.put("Q",12);
        map.put("K",13);
        map.put("A",0);
        Scanner scanner = new Scanner(System.in);
        List<Card> list = new LinkedList<>();
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            String[] card = s.split(" ");
            if (card.length!=2){
                System.out.println("输入错误，本次跳过");
                continue;
            }
            int num;
            try {
               num= Integer.parseInt(card[0]);
            }catch (Exception e){
                if (map.containsKey(card[0])){
                    num = map.get(card[0]);
                }else{
                    System.out.println("输入错误，本次跳过");
                    continue;
                }
            }
            StringBuilder sb = new StringBuilder();
            list.add(new Card(num,card[1].charAt(0)));
            if (list.size()==5){
                call(list);
                list = new LinkedList<>();
            }
        }
    }

    private static void call(List<Card> list) {
        Map<Character,List<Card>> colorMap = new HashMap<>();
        Map<Integer,List<Card>> numMap = new HashMap<>();
        for (Card card : list) {
            List<Card> list1 = colorMap.getOrDefault(card.color, new LinkedList<>());
            list1.add(card);
            colorMap.put(card.color,list1);
            List<Card> list2 = numMap.getOrDefault(card.num, new LinkedList<>());
            list2.add(card);
            numMap.put(card.num,list2);
        }
        boolean isSameColor = colorMap.keySet().size()==1;
        boolean isLineNum = numMap.keySet().size()==5 && isLineNum(numMap);
        if (isLineNum && isSameColor){
            System.out.println("同花顺");
            return;
        }
        if (isSameColor){
            System.out.println("同花");
            return;
        }
        if(isLineNum){
            System.out.println("顺子");
            return;
        }
        if (numMap.keySet().size()<4){
            int maxSize = 0;
            int minSize = 5;
            for (List<Card> value : numMap.values()) {
                if (value.size()>maxSize){
                    maxSize = value.size();
                }
                if (value.size()<minSize){
                    minSize = value.size();
                }
            }
            if (maxSize==4){
                System.out.println("四元");
                return;
            }
            if (maxSize==3 && minSize==2){
                System.out.println("三元两目");
                return;
            }
            if (maxSize==3){
                System.out.println("三元");
            }
        }
        System.out.println("杂牌");
    }





    private static boolean isLineNum(Map<Integer, List<Card>> numMap) {
        ArrayList<Integer> sortList = new ArrayList<>(numMap.keySet());
        Collections.sort(sortList);
        if (sortList.get(0)==0){
            if (sortList.get(4)-sortList.get(1)==3){
                return sortList.get(4)==13 || sortList.get(1)==1;
            }else{
                return false;
            }
        }else{
            return sortList.get(4)-sortList.get(0)==4;
        }
    }

    @AllArgsConstructor
    static class Card{
        int num;
        char color;

    }


    Node top;
    Node min;

    @Test
    public void test(){
        push(-2);
        push(0);
        push(-3);
        System.out.println(getMin());
        pop();
        System.out.println(top());
        System.out.println(getMin());
    }

    public void push(int val) {
        top = new Node(val,top);
        if(min == null){
            min = top;
        }else if(min.val > val){
            top.max = min;
            min.min = top;
            min = top;
        }else{
            Node index = min;
            while(index.max != null && index.max.val < val ){
                index = index.max;
            }
            top.max = index.max;
            top.min = index;
            index.max = top;
            if(top.max != null){
                top.max.min = top;
            }
        }
    }

    public void pop() {
        if(top != null){
            if(top == min){
                min = min.max;
            }else{
                top.min.max = top.max;
                if(top.max != null){
                    top.max.min = top.min;
                }
            }
            top = top.pre;
        }
    }

    public int top() {
        if(top == null){
            throw new RuntimeException("空");
        }
        int val = top.val;
        top = top.pre;
        if(top == min){
            min = min.max;
        }else{
            top.min.max = top.max;
            if(top.max != null){
                top.max.min = top.min;
            }
        }
        return val;
    }

    public int getMin() {
        if(min == null){
            throw new RuntimeException("空");
        }
        return min.val;
    }

    static class Node{
        int val;
        // 栈
        Node pre;
        // 有序队列。下一个比它大的值
        Node max;
        Node min;

        Node(int val ,Node pre){
            this.val = val;
            this.pre = pre;
        }
    }
}
