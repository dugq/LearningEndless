package com.dugq.arithmetic;

import com.alibaba.fastjson2.JSON;
import com.dugq.arithmetic.util.DoubleCounter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

    @Test
    public  void testCombinationSum(){
        System.out.println(JSON.toJSONString(combinationSum(new int[]{2,3,5},8)));
        System.out.println(JSON.toJSONString(combinationSum2(new int[]{2,3,5},8)));
        System.out.println(JSON.toJSONString(combinationSum(new int[]{2,3,6,7},7)));
        System.out.println(JSON.toJSONString(combinationSum2(new int[]{2,3,6,7},7)));
        System.out.println(JSON.toJSONString(combinationSum(new int[]{4,2,8},8)));
        System.out.println(JSON.toJSONString(combinationSum2(new int[]{4,2,8},8)));
    }
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> result = new LinkedList<>();
        int i =candidates.length-1;
        Integer dp[][] = new Integer[candidates.length][candidates.length];
        combinationSum(candidates,target,candidates.length-1,new ArrayList<>(),result,dp);
        return result;
    }

    // 给你一个 无重复元素 的整数数组 candidates 和一个目标整数 target ，找出 candidates 中可以使数字和为目标数 target 的 所有 不同组合 ，并以列表形式返回。你可以按 任意顺序 返回这些组合。

    void combinationSum(int[] candidates, int target,int stop,List<Integer> last, List<List<Integer>> result,Integer dp[][]){
        for(int i = stop; i>=0;i--){
            // 找到一个值=target，如果比目标值大，结束
            if(candidates[i]>target){
                continue;
            }else if(candidates[i]==target){
                List<Integer> list = new LinkedList<>();
                list.add(candidates[i]);
                list.addAll(last);
                result.add(list);
                continue;
            }
            for(int j =0; j<=i;j++){
                if(dp[i][j]==null){
                    dp[i][j] = candidates[i]+candidates[j];
                }
                if(dp[i][j]>target){
                    break;
                }else if(dp[i][j]==target){
                    // 两个值相加等于目标值的
                    List<Integer> list = new LinkedList<>(last);
                    list.add(candidates[i]);
                    list.add(candidates[j]);
                    result.add(list);
                }else{
                    // 两个值相加小于目标值的，再加一个
                    List<Integer> list = new LinkedList<>(last);
                    list.add(candidates[i]);
                    list.add(candidates[j]);
                    int target1 = target - dp[i][j];
                    combinationSum(candidates, target1,j,list,result,dp);
                }
            }
        }
    }

    List<List<Integer>> combinationSum2(int []candidates,int target ){
        List<List<Integer>> result = new LinkedList<>();
        LinkedList<Path> paths = new LinkedList<>();
        paths.add(new Path(target));
        while(paths.size()>0){
            Path path = paths.pop();
            for(int j = path.start ;j < candidates.length;j++){
                if (path.last == candidates[j]){
                    LinkedList<Integer> list = new LinkedList<>(path.list);
                    list.add(candidates[j]);
                    result.add(list);
                }
                if (path.last > candidates[j]){
                    paths.add(new Path(j,candidates[j],path));
                }
            }
        }
        return result;
    }

    class Path{
        int last;
        int start;
        LinkedList<Integer> list = new LinkedList<>();

        public Path(int target){
            this.last = target;
        }

        public Path(int start,int val, Path path){
            this.start = start;
            this.last=path.last-val;
            list.addAll(path.list);
            list.add(val);
        }

    }

    @Test
    public void combinationSumTest(){
        System.out.println(combinationSum3(new int[]{2,5,2,2},2));
    }

    public List<List<Integer>> combinationSum3(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> result = new LinkedList<>();
        LinkedList<Path> paths = new LinkedList<>();
        paths.add(new Path(target));
        while(paths.size()>0){
            Path path = paths.pop();
            for(int i = path.start;i<candidates.length;i++){
                // 第一层重复数字不重复找
                if(i>0 && candidates[i]==candidates[i-1]){
                    continue;
                }
                if(candidates[i]==path.last){
                    LinkedList<Integer> list = new LinkedList<>(path.list);
                    list.add(candidates[i]);
                    result.add(list);
                    break;
                }
                if(candidates[i]<path.last){
                    paths.add(new Path(i+1,candidates[i],path));
                }else{
                    break;
                }
            }
        }
        return result;
    }

    @Test
    public void testFirstMissingPositive(){
        System.out.println(firstMissingPositive(new int[]{2,2}));
    }

    public int firstMissingPositive(int[] nums) {
        for(int i=0;i<nums.length;){
            if(nums[i]<=0 || nums[i]>nums.length || nums[i]==i+1 || nums[nums[i]-1]==nums[i]){
                i++;
                continue;
            }
            int temp = nums[nums[i]-1];
            nums[nums[i]-1] = nums[i];
            nums[i] = temp;
        }
        for(int i=0;i<nums.length;i++){
            if(nums[i]!=i+1){
                return i+1;
            }
        }
        return nums.length+1;
    }

    @Test
    public void testPerMute(){
        System.out.println(JSON.toJSONString(permute(new int[]{1,2,3})));;
    }

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> last = new ArrayList<>();
        for(int i =0 ;i < nums.length;i++){
            last.add(nums[i]);
        }
        gen(last,new LinkedList<>(),result);
        return result;
    }

    public void gen(List<Integer> last,LinkedList<Integer> list,List<List<Integer>> result){
        if(last.size()==1){
            List<Integer> cr = new ArrayList<>(list);
            cr.add(last.get(0));
            result.add(cr);
            return;
        }
        for(int i =0; i<last.size();i++){
            Integer cu = last.remove(i);
            list.addLast(cu);
            gen(last,list,result);
            last.add(i,cu);
            list.removeLast();
        }
    }

    @Test
    public void testMyPow(){
        System.out.println(myPow(1.1,60));
    }


    public double myPow(double x, int n) {
        char[] value = new char[10];
        DoubleCounter counter = new DoubleCounter(1);
        counter.start();
        double result = x;
        boolean positive = true;
        if(n==0){
            return 1;
        }
        if(n<0){
            n = -n;
            positive = false;
        }
        int max = n-1;
        int step = 1;
        double stepNum = x;
        for(int i =0; i<max;){
            counter.incrementFirst();
            if(step<(max-i)/2){
                stepNum = stepNum*stepNum;
                step = step*2;
            }else if(step>max-i){
                counter.incrementSecond();
                step = 1;
                stepNum = x;
            }
            result = result * stepNum;
            i+=step;
        }
        counter.end().print();

        if(positive){
            return result;
        }
        return 1/result;
    }


    @Test
    public void testSolveNQueens() throws Exception{
        System.out.println(JSON.toJSONString(solveNQueens(4)));
    }

    public List<List<String>> solveNQueens(int n) {
        // 规律1: 每行必须存在一个，每列必须存在一个，每斜线上只能存在一个
        // 规律2: 棋盘可以视为一个二位数组，由于n<10，所以可以把棋盘的每个点的坐标计作一个两位十进制数字


        List<List<String>> result = new LinkedList<>();

        genRow(new HashSet<>(),0,n-1,result,new HashSet<>(),new ArrayList<>());

        return result;

    }

    // 从规律1可以得出 每行只能存在一个，也就是说每行有n中可能，我们逐个处理
    public void genRow(Set<Integer> addedLine,int index,int maxIndex,List<List<String>> result,Set<Integer> disableUsedPoint,List<String> gened){
        char[] chars = new char[maxIndex+1];
        for (int i = 0; i <= maxIndex; i++) {
            chars[i]='.';
        }
        // 每行的每列都可以尝试使用。只要不和disableUsedPoint冲突即可
        for(int i =0 ;i <=maxIndex;i++){
            if (index==2 && i==3){
                System.out.println();
            }
            // 每个点转为一个 2位十进制数字
            int point = i*10+index;
            if(disableUsedPoint.contains(point) || addedLine.contains(i)){
                continue;
            }
            chars[i]='Q';
            String str = new String(chars);
            chars[i]='.';
            if(index==maxIndex){
                List<String> list = new LinkedList<>(gened);
                list.add(str);
                result.add(list);
                continue;
            }

            // 把 左后斜线 和右后斜线加入到disableUsedPoint中
            Set<Integer> currentDisableUsedPoint = new HashSet<>(disableUsedPoint);
            int row = index;
            int line = i;
            // 右后方
            while(row++<maxIndex && line++<maxIndex){
                currentDisableUsedPoint.add(line*10+row);
            }
            row = index;
            line = i;
            //左后方
            while(line-->0 && row++<maxIndex){
                currentDisableUsedPoint.add(line*10+row);
            }

            // 追加本行新增的记录
            addedLine.add(i);
            gened.add(str);
            genRow(addedLine,index+1,maxIndex,result,currentDisableUsedPoint,gened);
            // 恢复现场，删除本行新增的记录
            addedLine.remove(i);
            gened.remove(str);
        }
    }

    @Test
    public void testMerge(){
        int[][] intervals = new int[][]{{1,3},{4,6},{3,4},{15,18}};
        System.out.println(JSON.toJSONString(merge(intervals)));
    }


    public int[][] merge(int[][] intervals) {
        LinkedList<Integer> hasValueIndex = new LinkedList<>();
        hasValueIndex.add(0);
        for (int i = 1; i < intervals.length; i++) {
            int size = hasValueIndex.size();
            int currentPos = i;
            for(int pos = 0;pos<size;pos++){
                int[] current = intervals[currentPos];
                Integer comPos = hasValueIndex.removeFirst();
                int[] compare = intervals[comPos];
                if (current[0]> compare[1] || current[1] < compare[0]) {
                    hasValueIndex.addLast(comPos);
                }else{
                    compare[0] = Math.min(compare[0], current[0]);
                    compare[1] = Math.max(compare[1], current[1]);
                    currentPos = comPos;
                }
            }
            hasValueIndex.add(currentPos);
        }
        int[][] result = new int[hasValueIndex.size()][2];
        int index = 0;
        for (Integer pos : hasValueIndex) {
            result[index++] = intervals[pos];
        }
        return result;
    }

    public int[][] insert(int[][] intervals, int[] newInterval) {
        LinkedList<int[]> result = new LinkedList<>();
        boolean added = false;
        for (int[] interval : intervals) {
            if (!added){
                if(newInterval[1]<interval[0]){
                    result.add(newInterval);
                    result.add(interval);
                    added = true;
                }
                else if(newInterval[0]>interval[1]){
                   result.add(interval);
                }else{
                    newInterval[1] = Math.max(newInterval[1],interval[1]);
                    newInterval[0] = Math.min(newInterval[0],interval[0]);
                    added = true;
                    result.add(newInterval);
                }
            }else{
                if (newInterval[1]>=interval[0]){
                    newInterval[1] = Math.max(newInterval[1],interval[1]);
                }else{
                    result.add(interval);
                }
            }
        }
        return result.toArray(new int[][]{});
    }


    @Test
    public void testPermutation(){
        for(int i=0;i<24;i++){
            System.out.println(getPermutation(4,i+1));
        }
    }

    public String getPermutation(int n, int k) {
        char[] chars = new char[n];
        // 记录 1！ ～ （n-1)!
        int dp[] = new int[n];
        dp[0] = 1;
        for(int i = 1; i < n; i++){
            dp[i] = dp[i-1] * i;
        }
        List<Integer> list = new ArrayList<>();
        //把1 ～ n倒叙放入队列中
        for(int i =0;i<n;i++){
            list.add(i+1);
        }
        // 每一位有n-i种可能，取第 k/（n-i-1)! 个数字
        for(int i = 0;i<n-1;i++){
            int pos = (k-1) /dp[n-i-1];
            int v = list.get(pos);
            chars[i] = (char)(v+'0');
            list.remove(pos);
            k = k<=dp[n-1-i]?k:k-dp[n-1-i]*pos;
        }
        chars[n-1] = (char)(list.get(0)+'0');
        return new String(chars);
    }

    @Test
    public void testUniquePaths(){
        System.out.println(uniquePaths(3,7));
    }
    public int uniquePaths(int m, int n) {
        int dp[][] = new int[m][n];
        dp[0][0] = 1;
        for(int i =0; i<m;i++){
            for(int j =0;j<n;j++){
                if(i>0){
                    dp[i][j] = dp[i][j] + dp[i-1][j];
                }
                if(j>0){
                    dp[i][j] = dp[i][j] + dp[i][j-1];
                }
            }
        }
        return dp[m-1][n-1];
    }

    @Test
    public void testMinPathSum(){
        int[][] grid = new int[][]{{1,3,1},{1,5,1},{4,2,1}};
        System.out.println(minPathSum(grid));
    }
    public int minPathSum(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int dp[][] = new int[m][n];
        for(int i =0; i< m;i++){
            for(int j =0;j<n;j++){
                if(i>0 && j>0){
                    dp[i][j] = grid[i][j] + Math.min(dp[i-1][j],dp[i][j-1]);
                }else if(i>0){
                    dp[i][j] = grid[i][j] + dp[i-1][j];
                }else if(j>0){
                    dp[i][j] = grid[i][j]+ dp[i][j-1];
                }else{
                    dp[i][j] = grid[i][j];
                }
            }
        }
        return dp[m-1][n-1];
    }

    @Test
    public void testIsNumber(){
        System.out.println(isNumber("."));
    }

    public boolean isNumber(String s) {
        return isNumber(0,s,true);
    }

    public boolean isNumber(int start,String s,boolean canHasE){
        boolean isPoint = false;
        boolean isLastNumber = false;
        boolean isNumber = false;
        for(int i =start; i< s.length();i++){
            if(s.charAt(i)=='+' || s.charAt(i)=='-'){
                if(i!=start){
                    return false;
                }
            }else if(s.charAt(i)=='.'){
                if(isPoint){
                    return false;
                }
                isPoint = true;
                if(isLastNumber){
                    continue;
                }else{
                    isNumber = false;
                }
            }else if(s.charAt(i)<'0' && s.charAt(i)>'9'){
                return false;
            }else if(s.charAt(i)=='E' || s.charAt(i)=='e'){
                return isNumber && canHasE && isNumber(i+1,s,false);
            }else{
                isLastNumber = true;
                isNumber = true;
            }
        }
        return isNumber;
    }

    @Test
    public void testAddBinary() {
        addBinary("1010","1011");
    }

    public String addBinary(String a, String b) {
        int added = 0;
        StringBuilder sb = new StringBuilder();
        for(int i =0; i<a.length() || i<b.length();i++){
            int current = added;
            added = 0;
            if(i<a.length()){
                current += a.charAt(i)-'0';
            }
            if(i<b.length()){
                current += b.charAt(i)-'0';
            }
            if(current==0){
                sb.insert(0,'0');
            }else if(current==1){
                sb.insert(0,'1');
            }else if(current==2){
                sb.insert(0,'0');
                added = 1;
            }else{
                sb.insert(0,'1');
                added = 1;
            }
        }
        if(added==1){
            sb.insert(0,'1');
        }
        return sb.toString();
    }

    @Test
    public void testFullJustify(){
        String[] words = new String[]{"This", "is", "an", "example", "of", "text", "justification."};
        System.out.println(fullJustify(words,16));
    }

    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new LinkedList<>();
        int currentLength = 0;
        int start = 0;
        for(int i =0; i< words.length;i++){
            String currentStr = words[i];
            currentLength = currentLength + currentStr.length();
            // 当前字符串放进去就超标。 字符串长度+ 两个子字符串一个空格（子字符串数-1个空格）
            if(currentLength + (i-start) > maxWidth){
                currentLength = currentLength - currentStr.length();
                result.add(appendBlank(maxWidth,currentLength,words,start,i-1,false));
                // 放不下时回滚
                currentLength = currentStr.length();
                start = i;
            }
        }
        // 追加最后一行
        result.add(appendBlank(maxWidth,currentLength,words,start,words.length-1,true));
        return result;
    }

    private String appendBlank(int maxWidth,int currentLength,String[] words,int start,int end,boolean left){
        int wordsSize = end - start + 1;
        //拼接新字符串
        //空格数 = maxWidth-currentLength
        //每个单词空格数 = wordsSize > 1 ?  (空格数 / i-start + 候补空格) ： 空格数
        StringBuilder sb = new StringBuilder(words[start]);
        int blankNums = maxWidth - currentLength;
        if(wordsSize==1){
            return appendBlank(sb,blankNums).toString();
        }
        if(left){
            for(int i =start+1; i<end;i++){
                blankNums--;
                sb.append(' ').append(words[i]);
            }
            if(blankNums>0){
                return appendBlank(sb,blankNums).toString();
            }
        }
        int blankNum = blankNums / (wordsSize - 1);
        int addBlank = blankNums - blankNum * (wordsSize-1);
        for(int j = 1; j<wordsSize;j++){
            //先追加空格，blankNum + j<addBlank ? 1 : 0;
            appendBlank(sb,(j<= addBlank ? 1 : 0) + blankNum);
            sb.append(words[start+j]);
        }
        return sb.toString();
    }

    private StringBuilder appendBlank(StringBuilder sb , int num){
        for(int i = 0; i< num; i++){
            sb.append(' ');
        }
        return sb;
    }
}
