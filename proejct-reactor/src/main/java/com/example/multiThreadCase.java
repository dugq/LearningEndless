package com.example;

public class multiThreadCase {

    /**
     * 需求： 查询某用户的订单信息，订单信息包括订单号、订单状态、订单金额等。
     *  <li>task1：查询用户订单信息</li>
     *   <li>task2：根据订单号查询商品信息</li>
 *      <li>task3：根据商品信息查询商品价格</li>
     *  <li>其他类似的查询订单信息的任务，task2依赖task1,task3依赖task2结果，有顺序的表示有依赖关系</li>
     *  <li>task10: 根据查询到所有信息汇总聚合成报表返回给用户</li>
     * <pre>
     *             |---> task2 ---> task3------------------|
     *   task1 ----|---> task4---> task5---> task6---------|------> task10
     *             |---> task7-------> task8---------------|
     *   </pre>
     * @param args
     */
    public static void main(String[] args) {

    }
}
