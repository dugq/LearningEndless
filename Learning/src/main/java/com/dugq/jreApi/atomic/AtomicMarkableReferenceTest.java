package com.dugq.jreApi.atomic;

import com.dugq.base.User;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * Created by dugq on 2023/4/25.
 */
public class AtomicMarkableReferenceTest {

    /**
     * 可以看作AtomicStampedReference的弱化版本
     */
    private AtomicMarkableReference<User> atomicUser ;

    @Test
    public void test(){
        atomicUser = new AtomicMarkableReference<>(new User(1),false);
        boolean value = atomicUser.compareAndSet(atomicUser.getReference(), new User(2), atomicUser.isMarked(), true);
        if (value){
            System.out.println("更新成功");
        }else{
            System.out.println("更新失败");
        }
    }

}
