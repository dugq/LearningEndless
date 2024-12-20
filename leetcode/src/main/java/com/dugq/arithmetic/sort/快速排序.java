package com.dugq.arithmetic.sort;

import com.dugq.arithmetic.util.DoubleCounter;
import com.dugq.arithmetic.util.MyArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by dugq on 2023/8/16.
 */
public class 快速排序 {
    private DoubleCounter doubleCounter = new DoubleCounter(1);

    @Test
    public void test(){
        int[] source = MyArrayUtils.randomIntArray(1000,10000);
        int[] array = Arrays.copyOf(source, source.length);
        fastSort(array).print("fast sort foreach times = ","swap times = ");
        双基准快排 quickSort = new 双基准快排();
        MyArrayUtils.validatorSortedArray(quickSort.sort(Arrays.copyOf(source,source.length)),source,false);
        MyArrayUtils.validatorSortedArray(array,source,false);
    }

    @Test
    public void testForeachVersion(){
        int[] source = MyArrayUtils.randomIntArray(10,100);
        int[] array = Arrays.copyOf(source, source.length);
        sortWithArray(array).print("fast sort foreach times = ","swap times = ");
        MyArrayUtils.validatorSortedArray(array,source,false);
    }

    public static DoubleCounter fastSort(int[] array){
        快速排序 test = new 快速排序();
        test.doubleCounter.start();
        test.sortWithArray(array);
        test.doubleCounter.end();
        return test.doubleCounter;
    }

    private void sort(int[] source, int left, int right) {
        if (right<=left){
            return;
        }
       int pivot = getPivot(source,left,right);
        sort(source,left,pivot-1);
       sort(source,pivot+1,right);


    }

    private class DoublePivot{
        int left;
        int right;

        public DoublePivot(int left, int right) {
            this.left = left;
            this.right = right;
        }
    }

    private DoubleCounter sortWithArray(int[] source){
        LinkedList<DoublePivot> pivots = new LinkedList<>();
        pivots.add(new DoublePivot(0,source.length-1));
        while (pivots.size()>0){
            DoublePivot next = pivots.poll();
            int pivot = getPivot(source,next.left,next.right);
            if (pivot+1<next.right){
                pivots.push(new DoublePivot(pivot+1,next.right));
            }
            if (pivot-1>next.left){
                pivots.push(new DoublePivot(next.left,pivot-1));
            }
        }
        return doubleCounter;
    }

    // 快排的核心： 将数组按照基准划分为 左小右大
    private int getPivot(int[] source, int left, int right) {
        // pivot 初始化随机选举
        int pivot = right;
        int location = left;
        for (int i = left; i < right; i++) {
            doubleCounter.incrementFirst();
            if (source[i] <= source[pivot]){
                MyArrayUtils.swapArray(source,location,i);
                doubleCounter.incrementSecond();
                location ++;
            }
        }
        MyArrayUtils.swapArray(source,location,pivot);
        doubleCounter.incrementSecond();
        return location;
    }

    @Test
    public void testQuickSort(){
        System.out.println(largestNumber(new int[]{1,2,3,4,5,6,7,8,9,0}));
    }


    Map<Integer, List<Integer>> map = new HashMap<>();
    public String largestNumber(int[] nums) {
        int len = nums.length;
        sort1(nums,0,len);
        String res = "";
        for(int i =0; i<len;i++){
            res = nums[i]+res;
        }
        return res;
    }

    // 排序 首位大的越大，首位相同的比较下一位，下一位为空的大，或者下一位大的大
    // 快排
    public void sort1(int[] nums,int start,int end){
        if(start+1>end){
            return;
        }
        int p = start;
        int loc = end-1;
        for(int i =start+1;i<=loc;){
            // nums[p]>nums[i] 把nums[i] 移动到前面
            if(compare(nums[i],nums[p])<0){
                swap(nums,i,p);
                p = i;
                i++;
                // 如果比基准线大，则移动到队尾
            }else{
                swap(nums,i,loc);
                loc--;
            }
        }
        sort1(nums,0,p);
        sort1(nums,p+1,end);
    }

    // return 1 :x>y
    // return -1 : x<=y
    public int compare(int x,int y){
        List<Integer> xQue =  getNums(x);
        List<Integer> yQue =  getNums(y);
        int pos = 0;
        while(true){
            if(xQue.size()==pos){
                if(yQue.size()==pos){
                    return 0;
                }
                return -compare(yQue,pos,yQue.get(pos));
            }
            if(yQue.size()==pos){
                return compare(xQue,pos,xQue.get(pos));
            }
            int xx = xQue.get(pos);
            int yy = yQue.get(pos);
            if(xx > yy){
                return 1;
            }else if(xx<yy){
                return -1;
            }
            pos++;
        }
    }

    public int compare(List<Integer> num,int pos,int p){
        for(int i =0; i<pos;i++){
            if(p>num.get(i)){
                return 1;
            }
            if(p<num.get(i)){
                return -1;
            }
        }
        return 0;
    }

    public List<Integer> getNums(int num){
        int old = num;
        List<Integer> list = map.get(num);
        if(list!=null){
            return list;
        }
        list = new LinkedList<>();
        if (num==0){
            list.add(0);
            return list;
        }
        while(num>0){
            int n = num%10;
            list.add(0,n);
            num = num/10;
        }
        map.put(old,list);
        return list;
    }

    public void swap(int[] nums,int i ,int j){
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    @Test
    public void testMaxNumber(){
        maxNumber(new int[]{6,7},new int[]{6,0,4},5);
    }


    public int[] maxNumber(int[] nums1, int[] nums2, int k) {
        int[] result = new int[k];
        int start1 = 0;
        int start2 = 0;
        int len1 = nums1.length;
        int len2 = nums2.length;
        for(int i =0; i<k;i++){
            int f = len2+len1-start1-start2-k+i;
            int max1 = getLargestPos(nums1,start1,start1+f+1);
            int max2 = getLargestPos(nums2,start2,start2+f+1);
            if(max2 == -1 || (max1>-1 && nums1[max1]>nums2[max2])){
                start1 = max1+1;
                result[i] = nums1[max1];
            }else{
                start2 = max2+1;
                result[i] = nums2[max2];
            }
        }
        return result;
    }

    public int getLargestPos(int[] nums,int start,int end){
        if(start>=nums.length){
            return -1;
        }
        int max = start;
        for(int i = start+1;i<end && i<nums.length;i++){
            if(nums[i]>nums[max]){
                max = i;
            }
        }
        return max;
    }
}
