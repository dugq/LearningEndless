package com.dugq.arithmetic.util;

import com.alibaba.fastjson.JSON;

import java.util.LinkedList;

/**
 * Created by dugq on 2024/2/27.
 */
public class TreeNode {
    public int val;
    public TreeNode left;
    public TreeNode right;

    TreeNode() {}
    public TreeNode(int val) { this.val = val; }
    public TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    // 从数组构建二叉树 (层级，非空行 节点为空时用null代替)
    public TreeNode(Integer[] values){
        this.val = values[0];
        LinkedList<TreeNode> line = new LinkedList<>();
        line.add(this);
        int index = 1;
        while(index < values.length){
            int lineSize = line.size();
            for(int i =0; i<lineSize;i++ ){
                TreeNode current = line.removeFirst();
                Integer value = values[index++];
                if (value != null){
                    current.left = new TreeNode(value);
                    line.add(current.left);
                }
                if (index >= values.length){
                    break;
                }
                value = values[index++];
                if (value != null){
                    current.right = new TreeNode(value);
                    line.add(current.right);
                }

            }
        }
    }

    public void add(int value){
        if(value < val){
            if(left==null){
                left = new TreeNode(value);
            }else{
                left.add(value);
            }
        }else if(value>val){
            if(right==null){
                right = new TreeNode(value);
            }else{
                right.add(value);
            }
        }else{
            throw new RuntimeException("value already exists");
        }
    }

    // 按层级输出，非空行的空节点用null占位
    public LinkedList<Integer> changeArray(){
        LinkedList<Integer> result = new LinkedList<>();
        LinkedList<TreeNode> line = new LinkedList<>();
        line.add(this);
        while(line.size()>0){
            int lineSize = line.size();
            boolean allNull = true;
            for(int i =0; i<lineSize;i++){
                TreeNode current = line.removeFirst();
                if (current==null){
                    result.add(null);
                    continue;
                }
                if(current.left != null || current.right != null){
                    allNull = false;
                }
                result.add(current.val);
                line.addLast(current.left);
                line.addLast(current.right);
            }
            if (allNull){
                break;
            }
        }
        return result;
    }

    public void print(){
        System.out.println(JSON.toJSONString(changeArray()));
    }
}
