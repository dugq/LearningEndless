package com.dugq;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by dugq on 2023/12/26.
 */
public class UnsafeUtil {

    public static Unsafe getUnsafe(){
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
