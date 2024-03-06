package com.dugq.arithmetic;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dugq on 2024/1/9.
 */
public class StringOp {

    @Test
    public void testFindSubString(){
        List<Integer> list = findSubstring("ccaacbbaabcbaddcddbbbddaaaacadbcbbcacdacbc", new String[]{ "bca","bcb","cda"});
        System.out.println(JSON.toJSONString(list));
    }

    public List<Integer> findSubstring(String s, String[] words) {
        Set<Character> set = new HashSet<>();
        for(int i =0;i<words.length;i++){
            set.add(words[i].charAt(0));
        }
        List<Integer> result = new ArrayList<>();
        LinkedList<ConditionString> conditions = new LinkedList<>();
        for(int i =0 ;i < s.length();i++){
            char current = s.charAt(i);
            int size = conditions.size();
            for(int j =0;j<size;j++){
                ConditionString condition = conditions.removeLast();
                int val = condition.compare(current);
                if(val==-2){
                    continue;
                }else if(val==-1){
                    conditions.addFirst(condition);
                }else{
                    result.add(val);
                }
            }
            if(set.contains(current)){
                if (words[0].length()==1 && words.length==1){
                    result.add(i);
                }else{
                    conditions.addFirst(new ConditionString(words,current,i));
                }
            }
        }
        return result;
    }

    class ConditionString{
        Map<Character,LinkedList<String>> map;
        LinkedList<String> compareWordsIndex;
        int comparedindex;
        int worldLength ;
        int startIndex;
        public ConditionString(String[] words,char current,int startIndex){
            this.map = new HashMap<>();
            this.worldLength = words[0].length();
            this.startIndex = startIndex;
            for(int i =0;i<words.length;i++){
                addMap(words[i]);
            }
            comparedindex = 1;
            compareWordsIndex = map.remove(current);
            if (worldLength==1){
                compareWordsIndex.removeFirst();
                addMap(compareWordsIndex);
            }

        }

        private void addMap(String s){
            LinkedList<String> list = map.get(s.charAt(0));
            if(list==null){
                list = new LinkedList<>();
                list.add(s);
                map.put(s.charAt(0),list);
            }else{
                list.add(s);
            }
        }

        public int compare(char current){
            if (worldLength==1){
                LinkedList<String> strings = map.remove(current);
                if (strings==null){
                    return -2;
                }
                strings.remove();
                if (strings.size()>0){
                    map.put(current,strings);
                    return -1;
                }
                if(map.size()==0){
                    return startIndex;
                }
                return -1;
            }
            if(comparedindex==0){
                LinkedList<String> remove = map.remove(current);
                if(remove==null || remove.size()==0){
                    return -2;
                }
                compareWordsIndex = remove;
                comparedindex++;
                return -1;
            }
            Iterator<String> iterator = compareWordsIndex.iterator();
            boolean sucess = false;
            while(iterator.hasNext()){
                String s = iterator.next();
                if(current!=s.charAt(comparedindex)){
                    iterator.remove();
                    addMap(s);
                    continue;
                }
                sucess = true;
                if(comparedindex==worldLength-1){
                    iterator.remove();
                    addMap(compareWordsIndex);
                    break;
                }
            }
            if(!sucess){
                return -2;
            }
            if(comparedindex++==worldLength-1){
                comparedindex = 0;
                if(map.size()==0){
                    return startIndex;
                }
            }
            return -1;
        }

        private void addMap(List<String> list) {
            if (list==null){
                return;
            }
            Iterator<String> iterator = list.iterator();
            while(iterator.hasNext()){
                addMap(iterator.next());
                iterator.remove();
            }
        }
    }

    @Test
    public void testMatch(){
        System.out.println("s = aa; p= aa;  match = "+isMatch("aa","aa"));
        System.out.println("s = aa; p= a;  match = "+isMatch("aa","a"));
        System.out.println("s = aa; p= a*;  match = "+isMatch("aa","a*"));
        System.out.println("s = aa; p= a*a;  match = "+isMatch("aa","a*a"));
        System.out.println("s = aab; p= a*a;  match = "+isMatch("aab","a*a"));
        System.out.println("s = ab; p= ?*;  match = "+isMatch("ab","?*"));
        System.out.println("s = aa; p= *a*a;  match = "+isMatch("aa","*a*a"));
        System.out.println("s = aa; p= *a;  match = "+isMatch("aa","*a"));
        System.out.println("s = ab; p= *a;  match = "+isMatch("ab","*a"));
        System.out.println("s = missingtest; p= mi*ing?s*t;  match = "+isMatch("missingtest","mi*ing?s*t"));
    }

    public boolean isMatch(String s, String p) {
        if (p.length()==0){
            return s.length()==0;
        }
        List<String> strs = new ArrayList<>();
        boolean endWith = p.charAt(p.length()-1)=='*';
        boolean startWith = p.charAt(0)=='*';
        int start = -1;
        for(int i=0; i<p.length();i++){
            if(p.charAt(i)=='*'){
                if (start!=i-1){
                    strs.add(p.substring(start+1,i));
                }
                start = i;
            }
        }
        if (start<p.length()-1){
            strs.add(p.substring(start+1));
        }
        if (strs.size()==0){
            return true;
        }
        int matchIndex = 0;
        for(int i = 0;i<s.length();i++){
            String p1 = strs.get(matchIndex);
            // 不以*开头的情况，第一个子串必须从0开始完全匹配
            if (matchIndex==0 && !startWith){
                if (s.charAt(i)==p1.charAt(i) || p1.charAt(i)=='?'){
                    if (i==p1.length()-1){
                        // 兼容只有一个子串的情况
                        if (strs.size()==1){
                            return endWith || i==s.length()-1;
                        }
                        matchIndex++;
                    }
                    continue;
                }else{
                    return false;
                }
            }
            // 处理最后一个子串
            if(matchIndex==strs.size()-1){
                if(endWith){ // 以*结尾
                    return matchSub(s,i, p1,true)>=0;
                }else{ // 从右往左找，找到了就行
                    return matchSub(s,i, p1,false)>=0;
                }
            }
            // 中间子串只需要最短匹配就行 也包括p以*开头且不只一个子串时的第一个子串
            if ((i=matchSub(s,i,p1,true))<0){
                return false;
            }
            matchIndex++;
        }
        return  p.equals("*");
    }

    // return match stop pos
    public int matchSub(String s ,int start,String p,boolean left){
        if(left){
            for(int i = 0;i<s.length()-start;i++){
                if(p.charAt(i)=='?' || p.charAt(i)==s.charAt(i+start)){
                    if(i==p.length()-1){
                        return i+start;
                    }
                    continue;
                }
                i = -1;
                start++;
            }
            return -1;
        }
        if(s.length()-start<p.length()){
            return -1;
        }
        for(int i =0;i<p.length();i++){
            char c = p.charAt(p.length() - i - 1);
            if(s.charAt(s.length()-i-1)!= c && c !='?'){
                return -1;
            }
        }
        return s.length()-1;
    }

    @Test
    public void testSimplifyPath(){
        System.out.println(simplifyPath("/a/../../b/../c//.//"));
    }

    public String simplifyPath(String path) {
        LinkedList<String> list = new LinkedList<>();
        int currentPathStart = 1;
        for(int i = 1; i <= path.length();i++){
            if(i == path.length() || path.charAt(i) =='/' ){
                if((i-currentPathStart)>0){
                    String currentPath = path.substring(currentPathStart,i);
                    if(currentPath.equals("..")){
                        if (list.size()>0){
                            list.removeLast();
                        }
                    }else if(currentPath.equals(".")){
                        // do nothing
                    }else{
                        list.addLast(currentPath);
                    }
                }
                currentPathStart = i+1;
            }
        }
        if(list.size()==0){
            return "/";
        }
        StringBuilder sb = new StringBuilder();
        for(String currentPath : list){
            sb.append("/").append(currentPath);
        }
        return sb.toString();
    }
}
