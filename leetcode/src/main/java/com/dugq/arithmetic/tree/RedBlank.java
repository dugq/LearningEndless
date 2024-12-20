package com.dugq.arithmetic.tree;


public class RedBlank {

    private Node root;

    public void insert(int val){
        if(root == null){
            root = new Node(val,null);
            return;
        }
        Node cur = root;
        while(true){
            if(val <= cur.val){
                if(cur.left == null){
                    cur.left = new Node(val,cur);
                    cur = cur.left;
                    cur.isRed = true;
                    break;
                }
                cur = cur.left;
            }else{
                if(cur.right == null){
                    cur.right = new Node(val,cur);
                    cur = cur.right;
                    cur.isRed = true;
                    break;
                }
                cur = cur.right;
            }
        }
        if(cur.parent.isRed){
            Node uncle;
            if(cur.parent.parent.left == cur.parent){
                uncle = cur.parent.parent.right;
            }else{
                uncle = cur.parent.parent.left;
            }
            if(uncle == null){
                cur.parent.isRed = false;
            }
        }

    }

    static class Node {
        private int val;
        private Node left;
        private Node right;
        private Node parent;
        private boolean isRed;

        public Node(int val,Node parent) {
            this.val = val;
            this.parent = parent;
        }
    }
}
