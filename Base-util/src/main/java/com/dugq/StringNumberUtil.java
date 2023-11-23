package com.dugq;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class StringNumberUtil {

    public static void main(String[] args) {
        printBinaryString(-4);
    }

    public static void printBinaryString(long num){
        String s = Long.toBinaryString(num);
        char[] chars = s.toCharArray();
        LinkedList<Character> sb = new LinkedList<>();
        int index = 0;
        for (int i=chars.length-1;i>=0;i-- ) {
            if (index++%8==0 && index!=1){
                sb.addFirst(',');
            }
            sb.addFirst(chars[i]);
        }
        sb.forEach(System.out::print);
        System.out.println("  = "+num);
        LinkedList<Byte> list =getByte(num);
        Iterator<Byte> iterator = list.descendingIterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next()+",");
        }
        System.out.println("  = "+num);
    }

    /**
     * 把Long 值 转化为二进制 再按字节切分为若干组，每组转为byte 类型
     * @return 地4位在前，高4位在后
     */
    public static LinkedList<Byte> getByte(long value){
        String s = Long.toBinaryString(value);
        char[] chars = s.toCharArray();
        LinkedList<Character> sb = new LinkedList<>();
        for (char character : chars) {
            sb.addFirst(character);
        }
        List<List<Character>> partition = ListUtils.partition(sb, 8);
        LinkedList<Byte> result = new LinkedList<>();
        for (List<Character> character : partition) {
            Collections.reverse(character);
            String binary = StringUtils.join(character.toArray());
            result.addLast(binary2Byte(binary));
        }
        return result;
    }

    /**
     * 把按字节获取的字节数组转化为long
     * @param bytes 低4位在前，高4位在后的字节数组
     * @return 十进制数值
     */
    public static long byte2Long(List<Byte> bytes){
        ArrayList<Byte> list = new ArrayList<>(bytes);
        Collections.reverse(list);
        String binary =list.stream().map(StringNumberUtil::byte2SBinary).collect(Collectors.joining());
        return Long.parseLong(binary,2);
    }

    public static long byte2Int(List<Byte> bytes){
        ArrayList<Byte> list = new ArrayList<>(bytes);
        Collections.reverse(list);
        String binary =list.stream().map(StringNumberUtil::byte2SBinary).collect(Collectors.joining());
        return new BigInteger(binary,2).intValue();
    }

    private static String byte2SBinary(byte b){
        StringBuffer sb = new StringBuffer();
        for (int i=7; i>=0;i--){
            sb.append((byte)(b>>i & 0x1));
        }
        return sb.toString();
    }


    private static byte binary2Byte(String binary){
        if (StringUtils.isBlank(binary) || binary.length()>8){
            return 0;
        }
        if (binary.length()<8){
            // 这东西贼jb坑，它内部依赖的是int
            return Byte.parseByte(binary,2);
        }
        if (binary.charAt(0)=='1'){ //负数 这时候转为Int 再 转为 byte 时就会数组越界
            return (byte)(Integer.parseInt(binary,2)-256);
        }
        return Byte.parseByte(binary,2);
    }


}
