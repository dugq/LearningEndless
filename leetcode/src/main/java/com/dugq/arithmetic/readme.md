# [排序算法](sort%2F%E6%8E%92%E5%BA%8F%E7%AE%97%E6%B3%95.md)
* 排序算法的选择标准
  * 1、时间复杂度。平均/最坏/最好场景下复杂度是可变化的。需要根据实际的场景灵活判断
  * 2、空间复杂度（原地排序的概念，整个排序过程中不额外申请空间）
  * 3、比较/交换的次数


# 优先级算法


# 淘汰算法
* [LRU算法](%E6%B7%98%E6%B1%B0%E7%AE%97%E6%B3%95%2FLRUCache.java)


# 常用算法
* 暴力破解
* 动态优化
* 贪心算法
* 双指针算法


# 考虑点：
* 边界点
* 极限情况
* 递归转for循环
  * 1、声明一个类型用以替换递归中参数传递。
  * 2、在for外再加上while(Stack.size()>0)
~~~java
public class Test{
   // for循环 
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

  // 递归
  public List<List<Integer>> combinationSum(int[] candidates, int target) {
    int len = candidates.length;
    List<List<Integer>> res = new ArrayList<>();
    if (len == 0) {
      return res;
    }

    Deque<Integer> path = new ArrayDeque<>();
    dfs(candidates, 0, len, target, path, res);
    return res;
  }

  private void dfs(int[] candidates, int begin, int len, int target, Deque<Integer> path, List<List<Integer>> res) {
    // target 为负数和 0 的时候不再产生新的孩子结点
    if (target < 0) {
      return;
    }
    if (target == 0) {
      res.add(new ArrayList<>(path));
      return;
    }

    // 重点理解这里从 begin 开始搜索的语意
    for (int i = begin; i < len; i++) {
      path.addLast(candidates[i]);

      // 注意：由于每一个元素可以重复使用，下一轮搜索的起点依然是 i，这里非常容易弄错
      dfs(candidates, i, len, target - candidates[i], path, res);

      // 状态重置
      path.removeLast();
    }
  }
}
~~~
* 重复vs不重复 
  * 原数据是否重复  source = [1,2,2,2]
  * 结果是否重复   result = [ [1,2] , [2,1] ]  
  * 每个点是否重复  source = [1,2]  result = [1,1,2,2]
  * 三种都满足时，先排序再操作是最优解
~~~
for(int i=0;int i < length;i++){
   for(int j=0; j< length; j++){
     // 每个点重复 && 结果重复
   }
}
for(int i=0;int i < length;i++){
   // arr 中如果有重复需要加上判断
   if(arr[i]==arr[i-1]){continue;}
   for(int j=i; j< length; j++){
     // 每个点重复 && 结果不重复
   }
 }
 
for(int i=0;int i < length;i++){
   // arr 中如果有重复需要加上判断
   if(arr[i]==arr[i-1]){continue;}
   for(int j=i+1; j< length; j++){
      if(arr[j]==arr[j-1]){continue;}
     // 每个点不重复 && 结果不重复
   }
}
~~~

# 二分法注意点
* 当节点数为偶数时，中点有两个，可以选择(left+right)/2 和 （left+right)/2 选择
* 当节点数为偶数时，无论怎么选择，中点都不可能遍历整个数组。（要么会忽略起点，要么会忽略中点）
  * 所以必须先对起点或者重点进行判断是否符合条件
