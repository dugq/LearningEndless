package test;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.spring.util.HttpClientUtils;
import org.junit.Test;

/**
 * Created by dugq on 2018/3/23.
 */
public class test {
    @Test
    public void test(){
        JSONObject jsonObject = HttpClientUtils.httpPostJSON("http://sp.test.fingercrm.cn/distribution-system-management/sm/dept/listAllOrgForInner", "{\"cloudId\":2}");
        System.out.println(jsonObject.toJSONString());
    }
}
