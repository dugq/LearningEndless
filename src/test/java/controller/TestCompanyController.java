package controller;

import basic.ControllerBasicTest;
import com.example.DemoApplication;
import com.example.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


/**
 * Created by dugq on 2017/7/6.
 */

public class TestCompanyController extends ControllerBasicTest {

    @Test
    public void testAdd1() throws Exception {
        String urlTemplate = "/user/register";
        User user = new User();
        ResultActions perform = postJSON(urlTemplate,user);
        perform.andExpect(jsonPath("code").value("0"));
        perform.andExpect(jsonPath("body.orgId").isNotEmpty());
        printResponse(perform);
    }

}
