package com.example.learn.jre.gc.CleanerTest;

import sun.misc.Cleaner;

public class CleanerTest {

    public void testCleaner(){
        /**
         * {@link java.nio.DirectByteBuffer} 139 行
         */
        Cleaner.create(new Object(),()->{
            //do free
        });
    }

}
