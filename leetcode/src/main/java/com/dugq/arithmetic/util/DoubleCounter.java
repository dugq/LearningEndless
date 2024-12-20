package com.dugq.arithmetic.util;

/**
 * Created by dugq on 2023/12/5.
 */
public class DoubleCounter {

    public DoubleCounter(int increment) {
        this.increment = increment;
    }

    private long countFirst;

    private long countSecond;

    private int increment;

    private long startTime;

    private long endTime;
    public void incrementFirst(){
        countFirst+=increment;
    }

    public void incrementSecond(){
        countSecond+=increment;
    }

    public long getCountFirst() {
        return countFirst;
    }

    public long getCountSecond() {
        return countSecond;
    }

    public void print(String s, String s1) {
        System.out.println(s +" "+countFirst+" "+s1+" "+countSecond+" cost times = "+(endTime-startTime)+"ms");
    }

    public void print() {
        end();
        System.out.println("fist count = "+countFirst+" "+" second count = "+countSecond+" cost times = "+(endTime-startTime)+"ms");
    }

    public void printFist(){
        end();
        System.out.println("count = "+countFirst+" cost times = "+(endTime-startTime)+"ms");
    }

    public void printFist(String s){
        end();
        System.out.println(s+" count = "+countFirst+" cost times = "+(endTime-startTime)+"ms");
    }

    public DoubleCounter start() {
        this.countFirst = 0;
        this.countSecond = 0;
        this.startTime = System.currentTimeMillis();
        return this;
    }

    public DoubleCounter end(){
        endTime = System.currentTimeMillis();
        return this;
    }

}
