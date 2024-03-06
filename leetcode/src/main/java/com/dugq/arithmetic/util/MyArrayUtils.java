package com.dugq.arithmetic.util;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by dugq on 2023/12/4.
 */
public class MyArrayUtils {


    public static void swapArray(int[] array , int i , int j){
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }


    public static int[] randomIntArray(int size,int maxValue){
        if (maxValue<=0){
            maxValue = Integer.MAX_VALUE;
        }
        HashSet<Integer> set = new HashSet<>();
        while (set.size()<size){
            set.add(RandomUtils.nextInt(0,maxValue));
        }
        int[] array = new int[size];
        int i =0;
        for (int v : set) {
            array[i++] = v;
        }
        return array;
    }

    public static boolean validatorSortedArray(int[] array,int[] source){
        // for 循环source，验证每个数字在array中都存在
        for(int i = 0; i< source.length; i++){
            int j = 0;
            for(;j < array.length; j++){
                if (source[i] == array[j]){
                    break;
                }
            }
            if (j == array.length){
                return false;
            }
        }

        for(int i =0 ;i < array.length-1; i++){
           if (array[i] > array[i+1]){
               return false;
           }
       }
       return true;
    }

    public static void validatorSortedArray(int[] array,int[] source,boolean printIfRight){
        if (validatorSortedArray(array,source)){
            if (printIfRight){
                System.out.println("-----------------------");
                System.out.println("source = "+ JSON.toJSONString(source));
                System.out.println("array = "+ JSON.toJSONString(array));
                System.out.println("-----------------------");
            }
            return;
        }
        System.err.println("-----------------------");
        System.err.println("error!");
        System.err.println("source = "+ JSON.toJSONString(source));
        System.err.println("array = "+ JSON.toJSONString(array));
        System.err.println("-----------------------");
    }

    public static char[][] readCharArray(String s){
        if(s.startsWith("[[")){
            s = s.substring(1, s.length()-1);
        }
        List<List<Character>>result = new ArrayList<>();
        List<Character> current = new ArrayList<>();
        for(int i =0; i<s.length(); i++){
            char c = s.charAt(i);
            if (c==']'){
                result.add(current);
                current = new ArrayList<>();
                continue;
            }
            if (c=='[' || c=='\'' || c=='"' || c==','){
            }else{
                current.add(c);
            }
        }

        char[][] chars = new char[result.size()][result.get(0).size()];
        for(int i = 0; i<result.size(); i++){
            List<Character> list = result.get(i);
            char[] c = new char[list.size()];
            for(int j = 0; j<list.size(); j++){
                c[j] = list.get(j);
            }
            chars[i] = c;
        }
        return chars;
    }

    public static List<List<Integer>> readIntList(String s){
        if(s.startsWith("[[")){
            s = s.substring(1, s.length()-1);
        }
        List<List<Integer>>result = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        for(int i =0; i<s.length(); i++){
            char c = s.charAt(i);
            if (c==']'){
                result.add(current);
                current = new ArrayList<>();
                continue;
            }
            if (c=='[' || c=='\'' || c=='"' || c==','){
            }else{
                current.add(c-'0');
            }
        }

        return result;
    }

    public static int[][] readIntArray(String s){
        List<List<Integer>> result = readIntList(s);
        int[][] chars = new int[result.size()][result.get(0).size()];
        for(int i = 0; i<result.size(); i++){
            List<Integer> list = result.get(i);
            int[] c = new int[list.size()];
            for(int j = 0; j<list.size(); j++){
                c[j] = list.get(j);
            }
            chars[i] = c;
        }
        return chars;
    }



    public static void main(String[] args) {
        char[][] chars = readCharArray("[[\"1\",\"0\",\"1\",\"1\",\"1\"],[\"0\",\"1\",\"0\",\"1\",\"0\"],[\"1\",\"1\",\"0\",\"1\",\"1\"],[\"1\",\"1\",\"0\",\"1\",\"1\"],[\"0\",\"1\",\"1\",\"1\",\"1\"]]");
        System.out.println(chars);
    }

}
