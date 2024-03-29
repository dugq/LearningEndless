package com.dugq.jreApi.guava;

import com.dugq.ThreadUtil;
import com.dugq.base.User;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by dugq on 2018/9/26 0026.
 */
public class CacheTest {
    private static final Cache<String, List> workCahce = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS).maximumSize(1000).build();

    static List list = Arrays.asList(0,1,2,3,4,5,6,7,8,9);
    static ExecutorService executorService = Executors.newFixedThreadPool(10);
    static String key = "0:1:{0}:10";
    static CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
    static Logger log = LoggerFactory.getLogger(CacheTest.class);

    public static void main(String[] args) {
        for (int i = 0 ;i < 1000; i++){
            for (int j = 0 ; j < 10; j++){
                executorService.submit(new test(i,j));
            }
        }
    }


    static class test implements Runnable {
        private int num;
        private int page;

        public test(int num,int page) {
            this.num = num;
            this.page = page;
        }

        @Override
        public void run() {
            try {
                List list = workCahce.get(MessageFormat.format(key,Integer.toString(page)), () ->{
                log.debug("num={},page={}",Integer.toString(num),Integer.toString(page));
                    List<Object> objects = Arrays.asList(CacheTest.list.get(page));
                    return objects;
                });
                int o = (int)list.get(0);
                if(!Objects.equals(o,page%10)){
                    log.error("num={},page={},content={}",Integer.toString(num),Integer.toString(page), o);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final com.github.benmanes.caffeine.cache.Cache<User, List> userCache = Caffeine.newBuilder()
            .expireAfterWrite(50, TimeUnit.SECONDS).maximumSize(1000).build();

    @Test
    public void testWeakReference() throws ExecutionException {
        User user = new User(102);
        List test = userCache.get(user, (k) -> Arrays.asList(0,1,2,3,4,5));
        System.out.println("test size = "+test.size());
        user = null;
        test = null;
        System.gc();
        ThreadUtil.sleep(10);
        System.gc();
        List test1 = userCache.getIfPresent(new User(102));
        System.out.println("test size = "+test1.size());

    }
}
