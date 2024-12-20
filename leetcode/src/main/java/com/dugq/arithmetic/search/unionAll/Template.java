package com.dugq.arithmetic.search.unionAll;

import com.dugq.arithmetic.util.MyArrayUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Template {

    public static void main(String[] args) {
        Template template = new Template();
        List<List<String>> list = MyArrayUtils.readStringList(" [[\"b\",\"a\"],[\"b\",\"c\"]]");
        System.out.println(list.size());
        template.calcEquation(list,new double[]{1.0,5.0}, MyArrayUtils.readStringList("[[]]"));
    }

    /*
     给你一个变量对数组 equations 和一个实数值数组 values 作为已知条件，
     其中 equations[i] = [Ai, Bi] 和 values[i] 共同表示等式 Ai / Bi = values[i] 。
     每个 Ai 或 Bi 是一个表示单个变量的字符串。
     */
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
            int equationsSize = equations.size();

            UnionFind2 unionFind = new UnionFind2(2 * equationsSize);
            // 第 1 步：预处理，将变量的值与 id 进行映射，使得并查集的底层使用数组实现，方便编码
            Map<String, Integer> hashMap = new HashMap<>(2 * equationsSize);
            int id = 0;
            for (int i = 0; i < equationsSize; i++) {
                List<String> equation = equations.get(i);
                String var1 = equation.get(0);
                String var2 = equation.get(1);

                if (!hashMap.containsKey(var1)) {
                    hashMap.put(var1, id);
                    id++;
                }
                if (!hashMap.containsKey(var2)) {
                    hashMap.put(var2, id);
                    id++;
                }
                unionFind.union(hashMap.get(var1), hashMap.get(var2), values[i]);
            }

            // 第 2 步：做查询
            int queriesSize = queries.size();
            double[] res = new double[queriesSize];
            for (int i = 0; i < queriesSize; i++) {
                String var1 = queries.get(i).get(0);
                String var2 = queries.get(i).get(1);

                Integer id1 = hashMap.get(var1);
                Integer id2 = hashMap.get(var2);

                if (id1 == null || id2 == null) {
                    res[i] = -1.0d;
                } else {
                    res[i] = unionFind.isConnected(id1, id2);
                }
            }
            return res;
        }

}
