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

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void end(){
        endTime = System.currentTimeMillis();
    }
}
