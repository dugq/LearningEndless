package com.dugq.arithmetic.sort;

import com.dugq.arithmetic.util.DoubleCounter;
import com.dugq.arithmetic.util.MyArrayUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by dugq on 2023/12/6.
 * 自研垃圾
 */
public class TreeSort {
    /**
     *                         48
     *              36                             65
     *         26             41                           84
     *    12       27                                                86
     *                                                                      88
     * @param args
     */
    public static void main(String[] args) {
        int[] source = MyArrayUtils.randomIntArray(10, 100);
        source = new int[]{48, 65, 84, 36, 86, 88, 41, 26, 27, 12};
        int[] array = Arrays.copyOf(source, source.length);
        new TreeSort().sort(array);
        MyArrayUtils.validatorSortedArray(array,source,true);
    }

    private DoubleCounter counter = new DoubleCounter(1);

    public static DoubleCounter treeSort(int[] array){
        TreeSort treeSort = new TreeSort();
        treeSort.counter.start();
        treeSort.sort(array);
        treeSort.counter.end();;
        return treeSort.counter;
    }

    public void sort(int[] array){
        Node root = new Node(array[0]);
        for (int i =1; i< array.length;i++){
            insert(root,array[i]);
        }
        Node index = root;
        while (index.hasLeft()){
            index = index.left;
            counter.incrementFirst();
        }
        for (int i =0; i< array.length; i++){
            array[i] = index.value;
            if (index.hasRight()){
                index = index.right;
                while (index.hasLeft()){
                    index = index.left;
                    counter.incrementFirst();
                }
            }else{
                int value = index.value;
                index = index.parent;
                while (index.value < value){
                    counter.incrementFirst();
                    if (index.isRoot()){
                        return;
                    }
                    index = index.parent;
                }
            }
        }
    }

    public void insert(Node root,int value){
        while (true){
            counter.incrementFirst();
            if (root.value<value){
                if (root.hasRight()){
                    root = root.right;
                }else{
                    root.right = new Node(value,root);
                    break;
                }
            }else{
                if (root.hasLeft()){
                    root = root.left;
                }else{
                    root.left = new Node(value,root);
                    break;
                }

            }
        }
    }

    class Node{
        private Node left;
        private Node right;
        private int value;

        private Node parent;

        public Node(int value) {
            this.value = value;
        }

        public Node(int value, Node parent) {
            this.value = value;
            this.parent = parent;
        }

        public boolean hasLeft(){
            return Objects.nonNull(left);
        }

        public boolean hasRight(){
            return Objects.nonNull(right);
        }

        public boolean isRoot(){
            return Objects.isNull(parent);
        }
    }

}
