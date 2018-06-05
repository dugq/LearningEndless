package com.example.HeadFirst.exteriorModel;

/**
 * Created by dugq on 2018/5/14.
 * 外观模式:提供一个统一的几口，用来访问子系统中的一群接口。 外观定义了一个高层接口，让子系统更容易使用。
 * eg1： 电脑启动开机键执行时，用户感知只是按一键，但实际该键位为先后让cpu启动，内存启动，显示屏启动等等。。。。
 * eg2： 某些电视机遥控器开关机键，会控制遥控器本身，电视机，机顶盒，甚至包括DVD等~
 */
public class MyExterior {

    class Computer{
        CPU cpu;
        Mermory mermory;
        Screen screen;


        public void start(){
            cpu.start();
            mermory.start();
            screen.start();
            System.out.println("all start");
        }
        public void end(){
            cpu.end();
            mermory.end();
            screen.end();
            System.out.println("all end");
        }
    }

    class CPU{
        public void start(){
            System.out.println("cpu am start");
        }
        public void end(){
            System.out.println("cpu am end");
        }
    }

    class Mermory{
        public void start(){
            System.out.println("Mermory am start");
        }
        public void end(){
            System.out.println("Mermory am end");
        }
    }

    class Screen{
        public void start(){
            System.out.println("Screen am start");
        }
        public void end(){
            System.out.println("Screen am end");
        }
    }


}
