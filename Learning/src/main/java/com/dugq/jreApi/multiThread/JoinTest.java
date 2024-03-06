package com.dugq.jreApi.multiThread;

import org.junit.jupiter.api.Test;

/**
 * Created by dugq on 2024/1/24.
 */
public class JoinTest {

    @Test
    public void testJoin() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.join();
    }

}
