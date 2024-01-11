package com.dugq.jreApi.atomic;

import com.dugq.base.User;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Created by dugq on 2023/12/28.
 */
public class AtomicStampedReferenceTest {

    @Test
    public void test(){
        AtomicStampedReference<User> atomicStampedReference = new AtomicStampedReference<>(new User(1),0);
        User reference = atomicStampedReference.getReference();
        boolean result = atomicStampedReference.compareAndSet(reference, new User(2), atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
        if (result){
            System.out.println("更新成功 stamp = "+atomicStampedReference.getStamp());
        }else{
            System.out.println("更新失败");
        }
    }
}
