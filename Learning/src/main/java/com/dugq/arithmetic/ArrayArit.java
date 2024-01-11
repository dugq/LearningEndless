package com.dugq.arithmetic;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created by dugq on 2024/1/4.
 */
public class ArrayArit {
    public static void main(String[] args) {
        // double l = findMedianSortedArrays(new int[]{1, 2}, new int[]{3, 4});
        // System.out.println(l);

        //System.out.println(longestPalindrome("cbbd"));

//        System.out.println(myAtoi("  - 42"));

        // System.out.println(isPalindrome(10));


    }
    // 最长不重复子串 “abcabcd” => "abcd": 4
    public static int lengthOfLongestSubstring(String s) {
        int maxlength = 0;
        HashMap<Character,Integer> hash = new HashMap<>();
        int lastStart = 0;
        for (int i = 0; i< s.length();i++){
            Integer repeat = hash.get(s.charAt(i));
            if (repeat!=null && repeat>=lastStart){
                maxlength = Math.max(maxlength, i - lastStart);
                lastStart = repeat +1;
            }
            hash.put(s.charAt(i),i);
        }
        maxlength = Math.max(maxlength,s.length()-lastStart);
        return maxlength;
    }

    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int size = nums1.length + nums2.length;
        int midP = size/2;
        int p1 = 0;
        int p2 = 0;
        int t[] = new int[size/2+1];
        for(int i =0 ;i < size/2+1; i++){
            if(p1>=nums1.length){
                t[i] = nums2[p2];
                p2++;
            }else if(p2 >= nums2.length){
                t[i] = nums1[p1];
                p1++;
            }else if(nums1[p1]<nums2[p2]){
                t[i] = nums1[p1];
                p1++;
            }else{
                t[i] = nums2[p2];
                p2++;
            }
        }
        if((size & 1) == 1){
            return t[size/2];
        }else{
            return (double) (t[size / 2 - 1] + t[size / 2]) / 2;
        }
    }


    public static String longestPalindrome(String s) {
        int maxLength =0;
        String maxPa = null;
        for(int i =0 ; i < s.length(); i++){
            int index = 0;
            while(isPalindrome(s,i-index,i+1+index)){
                index++;
            }
            if(index>0 && index*2 > maxLength){
                maxLength = index*2;
                maxPa = s.substring(i-index+1,i+index+1);
            }
            index = 1;
            while(isPalindrome(s,i-index,i+index)){
                index++;
            }
            if(index>1 && index*2+1 > maxLength){
                index--;
                maxLength = index*2+1;
                maxPa = s.substring(i-index,i+index+1);
            }
        }
        return maxPa;
    }

    public static boolean isPalindrome(String s,int left,int right){
        if(left<0 || right>=s.length()){
            return false;
        }
        return s.charAt(left) == s.charAt(right);
    }


    public static int myAtoi(String s) {
        boolean di = true;
        boolean numStart=false;
        int num = 0;
        for(int i =0 ;i < s.length();i++){
            if(!numStart && s.charAt(i)==' '){
                continue;
            }
            if(!numStart && s.charAt(i)=='-'){
                di = false;
                numStart = true;
            } else if(s.charAt(i)>='0' && s.charAt(i)<='9'){
                numStart = true;
                num = num*10+s.charAt(i)-'0';
            }else{
                break;
            }
        }
        if(!di){
            return num*-1;
        }else{
            return num;
        }
    }

    public static boolean isPalindrome(int x) {
        if (x<0 || (x %10 == 0 && x>0)){
            return false;
        }
        int reservtNum = 0;
        while(reservtNum<x){
            reservtNum = reservtNum*10 + x%10;
            x /= 10;
        }

        return reservtNum==x || reservtNum/10 == x;
    }

    @Test
    public void testNextPermutation(){
        int[] nums = {1, 3, 2};
        nextPermutation(nums);
        System.out.println(JSON.toJSONString(nums));
    }

    public void nextPermutation(int[] nums) {
        int length = nums.length;
        if(length==1){
            return;
        }
        // 找到最后一个递增的两位(nums[index-1]<nums[index])，找不到就全部首尾互换
        // 找到了，说明nums[index,length-1]是递减数列，把它变为递增数列
        // 最后交换nums[index-1,index]
        int index = nums.length-1;
        for(;index>0;index--){
            if(nums[index-1]<nums[index]){
                break;
            }
        }
        //降序队列，首位互换为增序队列
        if(index==0){
            for(int i =0; i<length/2;i++){
                swap(nums,i,length-i-1);
            }
            return;
        }
        boolean swap = false;
        //sort nums[index,length-1] 本身是递减数列，交换为递增数列
        int i =0;
        for( ;i<(length-index)/2;i++){
            swap(nums,index+i,length-i-1);
            if (!swap && nums[index+i]>nums[index-1]){
                swap(nums,index-1,index+i);
                swap = true;
            }
        }
        if (swap){
            return;
        }
        for (;i<length-index;i++){
            if (nums[index+i]>nums[index-1]){
                swap(nums,index-1,index+i);
                return;
            }
        }
    }

    private void swap(int[] array,int i,int j){
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    @Test
    public void testlongestValidParentheses(){
        System.out.println(longestValidParentheses(")(((((()())()()))()(()))("));;
    }
    public int longestValidParentheses(String s) {
        int maxLanth =0;
        LinkedList<Integer> list = new LinkedList<>();
        int currentMaxLength = 0;
        for(int i =0; i< s.length();i++){
            if(s.charAt(i)=='('){
                list.push(i);
                currentMaxLength++;
            }else if(list.size()==0){
                currentMaxLength = 0;
            }else{
                list.pop();
                currentMaxLength++;
                if(list.size()==0){
                    maxLanth = Math.max(maxLanth,currentMaxLength);
                }else{
                    maxLanth = Math.max(maxLanth,i-list.peek());
                }
            }

        }
        return maxLanth;
    }

    @Test
    public void searchTest(){
        System.out.println(search(new int[]{4,5,6,7,0,1,2},0));
    }

    public int search(int[] nums, int target) {
        int length = nums.length;
        if (nums.length==0){
            return -1;
        }
        if(target==nums[0]){
            return 0;
        }else if(target==nums[length-1]){
            return length-1;
        }else if((target < nums[0] && target > nums[length-1])
                || (target < nums[0] && nums[0] < nums[length-1])){
            return -1;
        }else{ // 二分查找
            int start = 0;
            int end = length-1;
            // target 是否在左边
            boolean left = target > nums[0];
            while(true){
                if(end - start<=1){
                    return -1;
                }
                int middleIndex = (start+end)/2;
                int middle = nums[middleIndex];
                if(middle==target){
                    return middleIndex;
                }
                // target在左边: end<旋转点 或者 target < middle 右边缩小
                // target在右边: middle > target
                if((left && (middle<nums[start] || target < middle)) || (!left && middle<nums[end] && middle > target)){
                    end = middleIndex;
                }else{
                    start = middleIndex;
                }
            }
        }
    }

    @Test
    public void testSearchRange(){
        System.out.println(JSON.toJSONString(searchRange(new int[]{0,1,2,3,4,4,4},2)));
        System.out.println(JSON.toJSONString(searchRange(new int[]{5,7,7,8,8,10},8)));
    }
    public int[] searchRange(int[] nums, int target) {
        int left = 0;  // nums[left]=target nums[left-1]<target
        int right = nums.length-1; // nums[right]=target nums[right+1]>target
        int middle,leftMiddle,rightMiddle;
        if(nums.length == 0 || nums[0]>target || nums[nums.length-1]<target){
            return new int[]{-1,-1};
        }
        if (nums[0]==target){
            middle = leftMiddle = rightMiddle = 0;
        }else if(nums[nums.length-1]==target){
            middle = leftMiddle = rightMiddle = nums.length-1;
        }else{
            middle = leftMiddle = rightMiddle = (left+right+1)/2;
        }

        while(left<right){
            if (nums[middle]<target){
                left = middle;
                leftMiddle = rightMiddle = middle = (left+right+1)/2;
                continue;
            }
            if (nums[middle]>target){
                right = middle;
                leftMiddle = rightMiddle = middle = (left+right+1)/2;
                continue;
            }
            if(nums[left]==nums[right]){
                break;
            }
            if(nums[left]!=target){
                int subMiddle = (left+leftMiddle+1)/2;
                if(subMiddle==leftMiddle){
                    left = leftMiddle;
                }if (nums[subMiddle]<target){
                    left = subMiddle;
                }else{
                    leftMiddle = subMiddle;
                }
            }
            if(nums[right]!=target){
                int subMiddle = (rightMiddle+right)/2;
                if(subMiddle==rightMiddle){
                    right = rightMiddle;
                }if (nums[subMiddle]>target){
                    right = subMiddle;
                }else{
                    rightMiddle = subMiddle;
                }
            }
        }
        if(nums[left] == target){
            return new int[]{left,right};
        }else{
            return new int[]{-1,-1};
        }
    }

    @Test
    public void testIsVaSudoku(){
        char[][] bord = new char[][]{
                new char[]{'.', '.', '4', '.', '.', '.', '6', '3', '.'},
                new char[]{'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                new char[]{'5', '.', '.', '.', '.', '.', '.', '9', '.'},
                new char[]{'.', '.', '.', '5', '6', '.', '.', '.', '.'},
                new char[]{'4', '.', '3', '.', '.', '.', '.', '.', '1'},
                new char[]{'.', '.', '.', '7', '.', '.', '.', '.', '.'},
                new char[]{'.', '.', '.', '5', '.', '.', '.', '.', '.'},
                new char[]{'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                new char[]{'.', '.', '.', '.', '.', '.', '.', '.', '.'}
        };
        System.out.println(isValidSudoku(bord));;
    }

    public boolean isValidSudoku(char[][] board) {
        PriorityQueue<ArrayArit> queue = new PriorityQueue<>();
        Set<Character>[]row = new HashSet[9];
        Set<Character> []line = new HashSet[9];
        Set<Character> [][]range = new HashSet[3][3];
        for(int i =0; i<9;i++){
            for(int j=0;j<9;j++){
                char c= board[i][j];
                if(c=='.'){
                    continue;
                }
                if(row[i]==null){
                    row[i] = new HashSet<>();
                }
                if(!row[i].add(c)){
                    return false;
                }
                if(line[j]==null){
                    line[j] = new HashSet<>();
                }
                if(!line[j].add(c)){
                    return false;
                }
                if(range[i/3][j/3]==null){
                    range[i/3][j/3] = new HashSet<>();
                }
                if(!range[i/3][j/3].add(c)){
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    public void testSolveSuduku(){
        char[][] board = new char[][]{
                new char[]{'5','3','.','.','7','.','.','.','.'},
                new char[]{'6','.','.','1','9','5','.','.','.'},
                new char[]{'.','9','8','.','.','.','.','6','.'},
                new char[]{'8','.','.','.','6','.','.','.','3'},
                new char[]{'4','.','.','8','.','3','.','.','1'},
                new char[]{'7','.','.','.','2','.','.','.','6'},
                new char[]{'.','6','.','.','.','.','2','8','.'},
                new char[]{'.','.','.','4','1','9','.','.','5'},
                new char[]{'.','.','.','.','8','.','.','7','9'}
        };
        if (solveSudoku(board)){
            System.out.println(JSON.toJSONString(board));
        }
        System.out.println("error sudu");

    }

    public boolean solveSudoku(char[][] board) {
        Set<Character>[] line = new HashSet[9];
        Set[] row = new HashSet[9];
        Set[][] range = new HashSet[3][3];
        ArrayList<Pos> blankPos = new ArrayList<>();
        for(int i =0;i<9;i++){
            for(int j =0;j < 9;j++){
                if(row[i]==null){
                    row[i] = new HashSet<>();
                }
                if(line[j]==null){
                    line[j] = new HashSet<>();
                }
                if(range[i/3][j/3]==null){
                    range[i/3][j/3] = new HashSet<>();
                }
                if(board[i][j]=='.'){
                    blankPos.add(new Pos(i,j));
                }else{
                    line[j].add(board[i][j]);
                    row[i].add(board[i][j]);
                    range[i/3][j/3].add(board[i][j]);
                }
            }
        }
        for(Pos p: blankPos){
            p.remove(row,line,range);
        }
        if(remove(blankPos,0,row,line,range)){
            for(Pos p : blankPos){
                board[p.i][p.j] = p.val;
            }
            return true;
        }
        return false;

    }

    public boolean remove(ArrayList<Pos> blankPos,int pos,Set[] row ,Set[] line,Set[][] range ){
        Pos p = blankPos.get(pos);
        for(int i =0; i < p.set.size();i++){
            char v = p.set.get(i);
            if (row[p.i].contains(v) || line[p.j].contains(v) || range[p.i/3][p.j/3].contains(v)){
                continue;
            }
            p.val = v;
            row[p.i].add(v);
            line[p.j].add(v);
            range[p.i/3][p.j/3].add(v);
            if(pos<blankPos.size()-1){
                if(remove(blankPos,pos+1,row,line,range)){
                    return true;
                }
                row[p.i].remove(v);
                line[p.j].remove(v);
                range[p.i/3][p.j/3].remove(v);
            }else{
                return true;
            }
        }
        return false;
    }

    class Pos{
        int i;
        int j;
        LinkedList<Character> set;
        char val;
        public Pos(int i ,int j){
            this.i = i;
            this.j = j;
            this.set = new LinkedList<>();
            this.set.add('1');
            this.set.add('2');
            this.set.add('3');
            this.set.add('4');
            this.set.add('5');
            this.set.add('6');
            this.set.add('7');
            this.set.add('8');
            this.set.add('9');
        }

        public boolean remove(Set[] row ,Set[] line ,Set[][] range){
            this.set.removeAll(row[i]);
            this.set.removeAll(line[j]);
            this.set.removeAll(range[i/3][j/3]);
            return this.set.size()>0;
        }
    }

    @Test
    public void testCountAndSay(){
        countAndSay(4);
    }

    public String countAndSay(int n) {
        if(n==1){
            return "1";
        }
        String s = countAndSay(n-1);
        String result = "";
        int charStart = 0;
        for(int i = 1 ; i< s.length(); i++){
            if(s.charAt(i)==s.charAt(i-1)){
                continue;
            }
            result+= i-charStart;
            charStart = i;
            result += s.charAt(i-1);
        }
        result += s.length()-charStart;
        result += s.charAt(s.length()-1);
        return result;
    }
}
