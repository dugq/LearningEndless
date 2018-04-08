package test;

import com.alibaba.fastjson.JSONObject;
import com.example.util.HttpClientUtils;
import com.example.util.HttpUtils;
import org.junit.Test;
import sun.net.www.http.HttpClient;

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
