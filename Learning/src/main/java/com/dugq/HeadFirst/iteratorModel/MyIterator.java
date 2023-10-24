package com.dugq.HeadFirst.iteratorModel;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Created by dugq on 2018/5/14.
 * 迭代器模式： 提供一种方法顺序访问一个聚合对象中的各个元素，而又不暴露其内部的表示。
 * java为我们提供了iterator和ListIterator接口，而大部分的集合也都实现了这两个接口。
 * eg： collection接口下的实现
 */
public class MyIterator {

    class MyIteratorTest implements Iterator{
        String[] content;  //内部数据结构。 通过iterator的遍历，不暴露内部结构
        int index; //坐标点
        int size; // content 的length

        @Override
        public boolean hasNext() {
            return index<size;
        }

        @Override
        public Object next() {
            return content[index];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachRemaining(Consumer action) {
            throw new UnsupportedOperationException();
        }
    }
}
