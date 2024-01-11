package com.dugq.pojo;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
import org.openjdk.jol.vm.VirtualMachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dugq on 2023/12/26.
 */
public class ObjectUtil {
    static final VirtualMachine vm = VM.current();

    public static long getMarkWorld(Object obj){
        try {
            return vm.getLong(obj, 0);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static void printClassData(Object obj){
        ClassLayout classLayout = ClassLayout.parseInstance(obj);
        System.out.println(classLayout.toPrintable());
    }


    static Map<String,String> stateMap = new HashMap<>();
    static {
        // 此时第三位表示是否处于锁定状态
        stateMap.put("101","偏向锁状态");
        stateMap.put("001","无锁状态");
        stateMap.put("00","轻量级锁");
        stateMap.put("10","重量级锁");
        stateMap.put("11","GC");
    }

    public static void printSynchronizedState(Object obj){
        printSynchronizedState("",obj);
    }
    public static void printSynchronizedState(String flag ,Object obj){
        long markWorld = getMarkWorld(obj);
        StringBuilder binaryString = new StringBuilder(Long.toBinaryString(markWorld));
        // 避免后续数组越界，再数字的前面追加几个0
        if (binaryString.length()<3){
            for (int i =0; i<4; i++){
                binaryString.insert(0, "0");
            }
        }
        String blockFlagBit = binaryString.substring(binaryString.length()-2);
            if (blockFlagBit.equals("01")){
                blockFlagBit = binaryString.substring(binaryString.length()-3);
            }
        System.out.println(flag + "  当前对象 处于"+stateMap.get(blockFlagBit)+"状态");
    }


    public static void main(String[] args) {
        int[] array = new int[]{2,1,3,4,6,5,7,14};
        new ObjectUtil().print(array);
    }

    public void print(int[] array){
        new ArrayList();
        if(array==null || array.length<5){
            return;
        }
        int threeCount = 0;
        for(int i=0; i < array.length-1;i++){
            for(int j = i+1 ; j< array.length-1;j++){
                for(int k = j+1; k < array.length-1; k++){
                    threeCount++;
                   for (int x = 0; x < array.length-1; x++){
                       if (x == i || x == j || x == k){
                           continue;
                       }
                       for (int y = x+1; y<array.length-1;y++){
                           if (y == i || y == j || y == k){
                               continue;
                           }
                           if (array[i] + array[j] + array[k] == array[x] * array[y]){
                               printResult(array,i,j,k,x,y);
                           }
                       }

                   }
                }

            }

        }
        System.out.println(threeCount);
    }

    public void printResult(int[] array,int a,int b,int c,int x,int y){
        String sb = array[a] +
                "+" +
                array[b] +
                "+" +
                array[c] +
                "=" +
                array[x] +
                "*" +
                array[y];
        System.out.println(sb);
    }




}
