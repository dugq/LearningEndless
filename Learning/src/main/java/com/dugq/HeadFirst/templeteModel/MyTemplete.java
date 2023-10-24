package com.dugq.HeadFirst.templeteModel;


import org.junit.Test;

/**
 * Created by dugq on 2018/5/14.
 * 模板模式：在一个方法中定义一个算法的骨架，而将一些步骤延迟到子类中。模板方法是的子类可以在不改变算法结构的情况下，重新定义算法中的某些步骤
 * eg： 饮料的制作步骤：1：倒水 2：加物品 3：打包装杯 4：交付
 * eg: abstractApplicationContext的refresh方法
 */
public class MyTemplete {

    @Test
   public void test(){
       //到店客户点了一杯绿茶
        AbstractDrinkTemplete order1 = new TeaDrink();
        //开始制作茶
        order1.doDrink();
        //饿了吗有客户定制了一杯草莓冰水
        AbstractDrinkTemplete order2 = new Strawberry();
        //开始制作
        order2.doDrink();
   }


     abstract class AbstractDrinkTemplete{
        public void doDrink(){
            addWater();
            addThings();
            pack();
            pay();
        }

         protected void pay() {
        }

         protected void pack() {
        }

         protected void addThings() {
        }

         protected void addWater() {
        }
    }

    class TeaDrink extends AbstractDrinkTemplete{
        @Override
        protected void pay() {
            System.out.println("端给客户~");
        }

        @Override
        protected void pack() {
            System.out.println("倒入茶杯");
        }

        @Override
        protected void addThings() {
            System.out.println("加茶叶");
        }

        @Override
        protected void addWater() {
            System.out.println("加开水");
        }
    }

    class Strawberry extends AbstractDrinkTemplete{
        @Override
        protected void pay() {
            System.out.println("网购 交付外卖小哥~");
        }

        @Override
        protected void pack() {
            System.out.println("用机器封装，包装袋打包");
        }

        @Override
        protected void addThings() {
            System.out.println("添加草莓汁~");
            System.out.println("加冰块~");
        }

        @Override
        protected void addWater() {
            System.out.println("加入少量冰水~");
        }
    }

}
