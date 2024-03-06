package com.dugq.arithmetic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dugq on 2024/2/4.
 */
public class 滑动窗口 {
    public static void main(String[] args) {
        滑动窗口 This = new 滑动窗口();
        System.out.println(This.minWindow("abABaBBBBb","abb"));
    }

    /**
     * 给你一个字符串 s 、一个字符串 t 。
     * 返回 s 中涵盖 t 所有字符的最小子串。
     * 如果 s 中不存在涵盖 t 所有字符的子串，则返回空字符串 "" 。
     * 注意：
     * 对于 t 中重复字符，我们寻找的子字符串中该字符数量必须不少于 t 中该字符数量。
     * 如果 s 中存在这样的子串，我们保证它是唯一的答案。
     */
    public String minWindow(String s, String t) {
        // 1、 把t变为set<char>
        // 双指针: start = 子串开始 end = 子串结束
        HashMap<Character,Integer> source = new HashMap<>();
        for(int i =0; i<t.length();i++){
            char current = t.charAt(i);
            if(!source.containsKey(current)){
                source.put(current,1);
            }else{
                source.put(current,source.get(current)+1);
            }
        }
        HashMap<Character,Integer> counter = new HashMap<>(source);

        int start = -1;
        int end = 0;
        Map<Character,Integer> charLength = new HashMap<>();
        for(int i = 0; i< s.length();i++){
            char current = s.charAt(i);
            if(source.containsKey(current)){
                Integer hasLength = charLength.get(current);
                if(hasLength==null){
                    hasLength = 0;
                }
                //首次遇到，直接加入，end 移动
                if(hasLength<source.get(current)){
                    end = i +1;
                    charLength.put(current,hasLength+1);
                    if(start==-1){
                        start = i;
                    }
                    if(counter.get(current)==1){
                        counter.remove(current);
                    }else{
                        counter.put(current,counter.get(current)-1);
                    }
                }else{
                    int index = start;
                    charLength.put(current,hasLength+1);
                    Map<Character,Integer> opCharLength = new HashMap<>(charLength);
                    // 如果和开头相同，则从start+1开始遍历，尝试缩短子串长度
                    while(true){
                        // 不在源串的字符，可以缩短
                        // 在子串中start后面的位置还有的，可以缩短
                        char key = s.charAt(index);
                        Integer len = opCharLength.get(key);
                        if(len==null){
                            index++;
                        }else if(len<=source.get(key)){
                            break;
                        }else{
                            opCharLength.put(key,len-1);
                            index++;
                        }
                    }
                    // 如果是更优秀的抉择，则进行选择，否则回滚start
                    if(counter.size()>0 || end-start >= i+1-index){
                        start = index;
                        end = i +1;
                        charLength = opCharLength;
                    }
                }
            }
        }
        if(counter.size()==0){
            return s.substring(start,end);
        }else{
            return "";
        }
    }
}
