package com.dugq.arithmetic.sort;

import com.dugq.arithmetic.util.MyArrayUtils;
import com.dugq.arithmetic.util.DoubleCounter;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by dugq on 2023/12/5.
 */
public class 堆排序 {
    DoubleCounter counter= new DoubleCounter(1);
    private int[] array;
    private 堆排序(int[] array){
        this.array = array;
    }

    public 堆排序() {
    }

    public static void main(String[] args) {
        for (int i =0 ;i < 10; i++){
            int[] source = MyArrayUtils.randomIntArray(10,20);
            int[] array = Arrays.copyOf(source, source.length);
            heapSort(array).print("heap sort foreach times = ","compare times = ");
            MyArrayUtils.validatorSortedArray(array, source,false);
        }

    }

    @Test
    public void test(){
        int[] array = {16, 0, 18, 19, 6, 7, 8, 11, 13, 14};
        heapSort(array).print("heap sort foreach times = ","compare times = ");
    }

    public static DoubleCounter heapSort(int[] array){
        return new 堆排序(array).heapSort();
    }

    public DoubleCounter heapSort(){
        counter.start();
        buildMaxHeap(array.length, (array.length - 1) / 2);
        for (int i = array.length-1;i>0; i--){
            counter.incrementSecond();
            MyArrayUtils.swapArray(array,0,i);
            adjustHeap(i,0);
        }
        counter.end();
        return counter;
    }

    private void buildMaxHeap(int maxIndex, int rootIndex) {
        for (int i=rootIndex; i>=0;i--){
            adjustHeap(maxIndex,i);
        }
    }

    private void adjustHeap(int maxIndex, int rootIndex){
        while (rootIndex < maxIndex){
            counter.incrementFirst();
            // 保证每个3叉都是根节点最大。
            int leftChildIndex = 2 * rootIndex + 1;
            int rightChildIndex = leftChildIndex+1;
            int largest = rootIndex;
            if (leftChildIndex < maxIndex && array[leftChildIndex] > array[largest]){
                largest = leftChildIndex;
            }
            if (rightChildIndex < maxIndex && array[rightChildIndex] > array[largest]){
                largest = rightChildIndex;
            }
            if (largest!=rootIndex){
                MyArrayUtils.swapArray(array,rootIndex,largest);
                counter.incrementSecond();
                rootIndex = largest;
            }else{
                break;
            }
        }
    }


}
