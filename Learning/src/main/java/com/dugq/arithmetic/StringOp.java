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

}
