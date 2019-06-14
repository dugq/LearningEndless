package com.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

/**
 * Created by dugq on 2019-04-16.
 */
public class DateUtils {

    public static Long getTodayZeroMillis(){
        return System.currentTimeMillis()- ( System.currentTimeMillis()+ TimeZone.getDefault().getRawOffset()) % (1000 * 3600 * 24);
    }

    public static Long getTodayZeroMillis1(){
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
       for (int i = 0 ; i <100000;i++){
           getTodayZeroMillis();
       }
        Long end = System.currentTimeMillis();
       System.out.println(start-end);
    }
}
