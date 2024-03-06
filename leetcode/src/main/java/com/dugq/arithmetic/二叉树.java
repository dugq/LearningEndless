package com.dugq.arithmetic;

import com.alibaba.fastjson.JSON;
import com.dugq.arithmetic.util.MyArrayUtils;
import com.dugq.arithmetic.util.TreeNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dugq on 2024/2/29.
 */
@Slf4j
public class 二叉树 {

    public static void main(String[] args) {
        二叉树 This = new 二叉树();
        TreeNode root = new TreeNode(new Integer[]{10,5,15,3,7,13,17,1,4,6,8,11,14,16,18});
//        System.out.println(This.isValidBST(root));
//        This.buildTree(new int[]{4,3,1,2,5},new int[]{1,2,3,4,5}).print();
        System.out.println(JSON.toJSONString(This.minimumTotal(MyArrayUtils.readIntList("[[2],[3,4],[6,5,9],[4,4,8,0]]"))));
    }


    // 而叉树的规则判定：
    //1、节点的左子树只包含 小于 当前节点的数。
    //2、节点的右子树只包含 大于 当前节点的数。
    //3、所有左子树和右子树自身必须也是二叉搜索树。
    public boolean isValidBST(TreeNode root) {
        if(root==null){
            return true;
        }
        return isValidBST(root,null,null);
    }

    public boolean isValidBST(TreeNode root, Integer left, Integer right) {
        if(root==null){
            return true;
        }
        log.info("root: {} left: {} right:{}",root.val,left,right);
        if(left!=null && root.val <= left){
            return false;
        }
        if(right!=null && root.val >= right){
            return false;
        }

        return isValidBST(root.left,left,root.val) && isValidBST(root.right,root.val,right);
    }


    public TreeNode buildTree(int[] preorder, int[] inorder) {
        TreeNode root = new TreeNode(preorder[0]);
        int inorderPos =0;
        for(int i =0; i< inorder.length;i++){
            if(inorder[i] == preorder[0]){
                inorderPos = i;
            }
        }
        buildSon(root,preorder,inorder,0,0,inorderPos,inorderPos+1, inorder.length);
        return root;
    }


    public int buildSon(TreeNode root,int[] preorder, int[] inorder,int pos,int inorderLeftStart,int inorderLeftEnd,int inorderRightStart,int inorderRightEnd) {
        if(pos==preorder.length-1){
            return pos;
        }
        // 根据先序遍历 => next 一定是 root的子节点，要么是左子，要么是右子
        int next = preorder[pos+1];
        boolean left = false;
        int nextInorderPos = -1;
        // 根据中序遍历，限制节点在中序数组中的位置
        //  inorderLeftStart <= 左子节点在中序数组的位置 < inorderLeftEnd
        // inorderRightStart <= 右子节点在中序数组的位置 < inorderRightEnd
        for(int i = inorderLeftStart ; i< inorderRightEnd;i++){
            if(inorder[i] == next){
                if(i<inorderLeftEnd){
                    left = true;
                }
                nextInorderPos = i;
                break;
            }
        }
        if(nextInorderPos==-1){
            return pos;
        }
        pos++;
        if(left){
            root.left = new TreeNode(preorder[pos]);
            // 递归查找左子节点的子节点
            int last = buildSon(root.left,preorder,inorder,pos,inorderLeftStart,nextInorderPos,nextInorderPos+1,inorderLeftEnd);
            if(last<preorder.length-1){
                int rightInorderPos = -1;
                for(int i = inorderRightStart;i<inorderRightEnd;i++){
                    if(inorder[i]==preorder[last+1]){
                        rightInorderPos = i;
                        break;
                    }
                }
                if (rightInorderPos==-1){
                    return last;
                }
                root.right = new TreeNode(preorder[last+1]);
                return buildSon(root.right,preorder,inorder,last+1,inorderRightStart,rightInorderPos,rightInorderPos+1,inorderRightEnd);
            }

        }else{
            root.right = new TreeNode(preorder[pos]);
            return buildSon(root.right,preorder,inorder,pos,inorderRightStart,nextInorderPos,nextInorderPos+1,inorderRightEnd);
        }
        return pos;
    }

    // 杨辉三角
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> result = new LinkedList<>();
        List<Integer> last=null;
        for(int i = 0; i< numRows;i++){
            List<Integer> list = new ArrayList<>();
            list.add(1);
            for(int j = 1; j<i+1;j++){
                if(j==i){
                    list.add(1);
                }else{
                    list.add(last.get(j-1)+last.get(j));
                }
            }
            last = list;
            result.add(list);
        }
        return result;
    }

    public int minimumTotal(List<List<Integer>> triangle) {
        return lineToltal(triangle,1,null);
    }

    // index : 行数 1开始
    //
    public int lineToltal(List<List<Integer>> triangle,int index,List<Integer> parentSum){
        List<Integer> row = triangle.get(index-1);
        List<Integer> rowSum = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        for(int i =0; i<index;i++){
            if(index==1){
                rowSum.add(row.get(0));
                min = row.get(0);
                continue;
            }
            int current = row.get(i);
            int sum = Integer.MAX_VALUE;
            if(i>0){
                sum = parentSum.get(i-1)+current;
            }

            if(i<index-1){
                sum = Math.min(sum,parentSum.get(i)+current);
            }
            rowSum.add(sum);
            min = Math.min(sum,min);
        }
        if(index==triangle.size()){
            return min;
        }
        return lineToltal(triangle,index+1,rowSum);
    }

    @Test
    public void test(){
        isPalindrome("0P");
    }

    public boolean isPalindrome(String s) {
        int left = 0;
        int right = s.length()-1;
        while(left<right){
            char l = s.charAt(left);
            if((l<'A' || l >'Z') && (l<'a' || l >'z')){
                left++;
                continue;
            }
            char r = s.charAt(right);
            if((r<'A' || r >'Z') && (r<'a' || r >'z')){
                right--;
                continue;
            }
            if(l<='Z'){
                l = (char)(l+32);
            }
            if(r<='Z'){
                r = (char)(r+32);
            }
            if(l!=r){
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}
