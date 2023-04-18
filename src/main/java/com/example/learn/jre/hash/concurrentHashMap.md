# ConcurrentHashMap
* table的初始化在什么时候
* 插入元素的过程
    * 在什么时候加锁
    * 以什么为锁
    * 扩容时插入如何处理
* 删除元素如何处理
    * 被删掉的Node在什么时候移除
* table 扩容
    * sizeCtrl字段的意义
    * 扩容如何加锁
    * 扩容的过程
    * 多线程如何协作扩容
    * 扩容后元素如何移动
    
## 初始化
当ConcurrentHashMap构造器传入容量时，它会调用tabSizeFor方法计算出一个2的n次方数字
 * tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1)，为了保证一定能容下给定的目标值，它会对容量再增加一半+1，加1为了保证单数时也超过一半
 * 构造方法只是初始化了sizeCtrl属性，最终在第一次put元素时延迟初始化
 * 在初始化时 通过CAS（sizeCtrl, sizeCtrl, -1）来控制并发初始化
     
## 扩容并发处理
* 扩容的移位是分片段执行的，多线程可以分别处理不同片段的桶。
~~~java
public class ConcurrentHashMap{
    //transferIndex 扩容处理的索引
    //扩容处理从高位向地位开始，transferIndex初始值 = tab的长度
    // 比如线程A需要处理从 32 -> 16 这个片段的数据，则通过CAS把transferIndex从32修改为16，成功则继续进行处理
  private transient volatile int transferIndex;

    void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab){
        // stride 片段长度，即处理的tab数组中元素个数
        int n = tab.length, stride;
        if (nextTab == null) {            // initiating
            try {
                Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
                nextTab = nt;
            } catch (Throwable ex) {      // try to cope with OOME
                sizeCtl = Integer.MAX_VALUE;
                return;
            }
            nextTable = nextTab;
            //transferIndex初始值 = tab的长度
            transferIndex = n;
        }
        // 片段的长度和CPU核心数有关，最小值为16。
        // 当数量很多时，本行代码希望有CPU个数个线程来同时并发进行处理，以达到最佳速度
      if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
        stride = MIN_TRANSFER_STRIDE; // subdivide range
      // advance 作为一个标记，意义为是否需要进行工作分区，或者说需要重新划分工作分区
      boolean advance = true;
      boolean finishing = false; 
      // 这里进行死循环，一个片段处理完，它继续处理下个片段，直到处理finishing = true
      for (int i = 0, bound = 0;;) {
          // 这里避免竞争，会一直进行死循环尝试获取一个片段进行处理
          while (advance) {
            int nextIndex, nextBound;
            //已经处理完了，或者超出了数组下界时，就跳出
            if (--i >= bound || finishing)
                advance = false;
            //索引已经到达0了，说明处理完毕，也跳出
            else if ((nextIndex = transferIndex) <= 0) {
                i = -1;
                advance = false;
            }
            // 通过CAS操作，获得片段处理权
            else if (U.compareAndSwapInt
                     (this, TRANSFERINDEX, nextIndex,
                      nextBound = (nextIndex > stride ?
                                   nextIndex - stride : 0))) {
                //片段的终点
                bound = nextBound;
                //片段的起点
                i = nextIndex - 1;
                advance = false;
            }
          }
        
      }

    }
}
~~~            
    
![](./resource/ConcurrentHashMap.png)    
 
    
      
