package com.dugq.arithmetic;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * Created by dugq on 2024/3/1.
 */
public class 搜索算法 {


    public static void main(String[] args) {
        搜索算法 This = new 搜索算法();
        List<String> list = new ArrayList<>(Arrays.asList("hot", "dot", "dog", "lot", "log","cog"));
//        System.out.println(JSON.toJSONString(This.findLadders("hit","cog", list)));

//        System.out.println(This.longestConsecutive(new int[]{-7,-1,3,-9,-4,7,-3,2,4,9,4,-9,8,-7,5,-1,-7}));
        System.out.println(JSON.toJSONString(This.minCut("aab")));
    }

    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> result = new LinkedList<>();
        // 构建一个环形图
//        Map<String,List<String>> map = new HashMap<>();
//        for(int j =0; j<wordList.size();j++){
//            String s1 = wordList.get(j);
//            if(isOneDiff(beginWord,s1)){
//                List<String> list = map.getOrDefault(beginWord,new LinkedList<>());
//                list.add(s1);
//                map.put(beginWord,list);
//            }
//            for(int k = j; k < wordList.size(); k++){
//                String s2 = wordList.get(k);
//                if(isOneDiff(s2,s1)){
//                    List<String> list = map.getOrDefault(s1,new LinkedList<>());
//                    list.add(s2);
//                    map.put(s1,list);
//
//                    List<String> list2 = map.getOrDefault(s2,new LinkedList<>());
//                    list2.add(s1);
//                    map.put(s2,list2);
//                }
//            }
//        }

//        if(!map.containsKey(beginWord) || !found){
//            return result;
//        }
//        find(result,beginWord,endWord,new LinkedList<>(),new HashSet<>(),map);

        Map<String,List<String>> map = new HashMap<>();
        for(int i =0; i< wordList.size();i++){
            String s = wordList.get(i);
            for(int j = i+1;j< wordList.size();j++){
                String s2 = wordList.get(j);
                if(isOneDiff(s, s2)){
                    List<String> list = map.getOrDefault(s,new LinkedList<>());
                    list.add(s2);
                    map.put(s,list);

                    list = map.getOrDefault(s2,new LinkedList<>());
                    list.add(s);
                    map.put(s2,list);
                }
            }
        }
        if (!map.containsKey(endWord)){
            return result;
        }
        if (!map.containsKey(beginWord)){
            for (int i=0; i< wordList.size();i++){
                if (isOneDiff(wordList.get(i),beginWord)){
                    List<String> list = map.getOrDefault(beginWord,new LinkedList<>());
                    list.add(wordList.get(i));
                    map.put(beginWord,list);
                }
            }
        }

        Deque<Deque<String>> queue = new LinkedList<>();
        LinkedList<String> first = new LinkedList<>();
        first.add(beginWord);
        queue.add(first);
        int step = 0;
        boolean found = false;
        while (!queue.isEmpty() && step++<wordList.size()) {
            int size = queue.size();
            for (int i =0;i<size;i++) {
                Deque<String> q = queue.removeFirst();
                String s1 = q.peekLast();
                List<String> strings = map.get(s1);
                if (strings==null){
                    continue;
                }
                strings.removeAll(q);
                if(strings.size()==0){
                    continue;
                }
                for(String s2 : strings){
                    LinkedList<String> list = new LinkedList<>(q);
                    list.add(s2);
                    queue.addLast(list);
                    if (s2.equals(endWord)) {
                        found = true;
                        result.add(list);
                    }
                }
            }
            if (found){
                break;
            }
        }

        return result;
    }

    public boolean isOneDiff(String s,String s2){
        int diff = 0;
        for(int i = 0; i<s.length(); i++){
            if(s.charAt(i)!=s2.charAt(i)){
                diff++;
            }
            if(diff>1){
                return false;
            }
        }
        return diff==1;
    }

    public void find(List<List<String>> result, String beginWord, String endWord, Deque<String> addedQueue, Set<String> addedSet, Map<String,Set<String>> map){
        addedQueue.addLast(beginWord);
        addedSet.add(beginWord);

        if(Objects.equals(beginWord, endWord)){
            result.add(new ArrayList<>(addedQueue));
            addedQueue.pollLast();
            addedSet.remove(beginWord);
            return;
        }
        Set<String> next = map.getOrDefault(beginWord,new HashSet<>());
        for(String s : next){
            if(addedSet.contains(s)){
                continue;
            }
            find(result,s,endWord,addedQueue,addedSet,map);
        }
        addedQueue.pollLast();
        addedSet.remove(beginWord);
    }




    public int longestConsecutive(int[] nums) {
        int max = 0;
        Map<Integer,Integer> firstMap = new HashMap<>();

        Map<Integer,Integer> lastMap = new HashMap<>();
        Set<Integer> set = new HashSet<>();
        for(int i = 0; i<nums.length;i++){
            if (set.contains(nums[i])){
                continue;
            }else{
                set.add(nums[i]);
            }
            int current = nums[i];
            int next = current+1;
            int last = current-1;
            Integer lastCount = lastMap.remove(last);
            Integer nextCount = firstMap.remove(next);
            int count = 1;
            int start = current;
            int end = current;
            if(lastCount!=null){
                count += lastCount;
                start = current-lastCount;
            }
            if(nextCount!=null){
                count+=nextCount;
                end = current+nextCount;
            }
            firstMap.put(start,count);
            lastMap.put(end,count);
            max = Math.max(max,count);
        }
        return max;
    }


    public List<List<String>> partition(String s) {
        List<List<String>> result = new LinkedList<>();
        int len = s.length();
        boolean[][] dp = new boolean[len][len+1];
        for(int i = 1; i<=len;i++){
            for(int j = 0;j<=len-i;j++){
                if(s.charAt(j) == s.charAt(j+i-1)){
                    if(i>2){
                        dp[j][j+i] = dp[j+1][j+i-1];
                    }else{
                        dp[j][j+i] = true;
                    }
                }

            }
        }

        sub(0,dp,len,result,new LinkedList<>(),s);
        return result;
    }


    public void sub(int start,boolean[][] dp,int len,List<List<String>> result,Deque<String> parent,String s){
        if(start==len){
            result.add(new ArrayList<>(parent));
            return;
        }
        for(int i =start+1; i<=len;i++){
            if(dp[start][i]){
                String subs = s.substring(start,i);
                parent.addLast(subs);
                sub(i,dp,len,result,parent,s);
                parent.removeLast();
            }
        }
    }


    public int minCut(String s) {
        int len = s.length();
        boolean[][] dp = new boolean[len][len];
        for(int i =0; i<len;i++){
            for(int j =0;j<len-i;j++){
                dp[j][j+i] = s.charAt(j)==s.charAt(j+i) && (i<2 || dp[j+1][j+i-1]);
            }
        }
        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);
        int step = 0;
        while(true){
            int size = queue.size();
            for(int i = 0; i<size;i++){
                int start = queue.poll();
                for(int j = start;j<len;j++) {
                    if (dp[start][j]) {
                        if (j == len - 1) {
                            return step;
                        }
                        queue.offer(j + 1);
                    }
                }
            }
            step++;

        }
    }

}
