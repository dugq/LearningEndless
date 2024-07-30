package com.dugq.arithmetic.sort;


import com.dugq.arithmetic.util.DoubleCounter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SortQues {
    static DoubleCounter counter = new DoubleCounter(1);
    static DoubleCounter counter2 = new DoubleCounter(1);
    public static void main(String[] args) {
        SortQues sortQues = new SortQues();
//        System.out.println(sortQues.largestNumber(new int[]{93, 30, 34, 5, 9}));
        counter.start();
        sortQues.countPrimes(499979);
        counter.printFist();
        counter2.start();
        sortQues.countPrimes2(499979);
        counter2.printFist();
    }

    public String largestNumber(int[] nums) {
        return Arrays.stream(nums).mapToObj(Integer::valueOf).sorted(Comparator.comparing(Function.identity(),this::compareNum)).map(String::valueOf).collect(Collectors.joining());
    }

    public int compareNum(int num1,int num2){
        int  n1 = num1;
        int n2 = num2;
        int r1 = n1;
        int r2 = n2;
        while((n2=n2/10)>0){
            r1=r1*10;
        }
        r1 = r1*10 + num2;
        while((n1=n1/10)>0){
            r2=r2*10;
        }
        r2 = r2*10 + num1;
        return r1 > r2 ? 1 : -1;
    }




    public int countPrimes(int n) {
        if(n<3){
            return 0;
        }
        List<Integer> primes = new LinkedList<>();
        primes.add(2);
        for(int i = 3 ; i < n;i++){
            if(isPrime(i,primes)){
                primes.add(i);
            }
        }
        return primes.size();
    }

    public boolean isPrime(int num ,List<Integer> primes){
        for(int prime : primes){
            if(prime*prime>num){
                return true;
            }
            counter.incrementFirst();
            if(num%prime==0){
                return false;
            }
        }
        return true;
    }

    public int countPrimes2(int n) {
        int ans = 0;
        for (int i = 2; i < n; ++i) {
            ans += isPrime(i) ? 1 : 0;
        }
        return ans;
    }

    public boolean isPrime(int x) {
        for (int i = 2; i * i <= x; ++i) {
            counter2.incrementFirst();
            if (x % i == 0) {
                return false;
            }
        }
        return true;
    }



}
