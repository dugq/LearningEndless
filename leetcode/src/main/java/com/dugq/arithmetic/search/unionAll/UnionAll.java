package com.dugq.arithmetic.search.unionAll;

import java.util.Arrays;

//无序并查集

/**
 * 并查集
 * 目的： 判断无序图中，是否有环
 * 要求： 无序，无环
 * 实现： 把图变为2层的树，记录每个节点的父节点。
 *      如果连接的两个节点存在同一个父节点，那必然存在环了。
 * 注意：
 *  1、 无法用于有序图，因为有序图中，一个节点的可能存在多个父节点，parent数组无法记录
 *  2、 有序图中，存在顺序关系。
 *
 */
public class UnionAll {

    int[] parent;
    // 可以用rank 优化路径压缩
    int[] rank;

    public UnionAll(int n) {
        parent = new int[n];
        Arrays.fill(parent,-1);
        rank = new int[n];
    }

    public int find(int index){
        int p = parent[index];
        if (p!=-1){
            return parent[index] = find(p);
        }
        return index;
    }

    public boolean merge(int x, int y){
        int p1 = find(x);
        int p2 = find(y);
        if(p1==p2){
            return  false;
        }
        if(rank[p1] == rank[p2]){
            parent[p2] = p1;
            rank[p1]++;
        }else if(rank[p1] < rank[p2]){
            parent[p1] = p2;
            rank[p2]++;
        }else{
            parent[p2] = p1;
            rank[p1]++;
        }
        return true;
    }
}
