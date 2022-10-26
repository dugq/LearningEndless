package controller;

import basic.ControllerBasicTest;
import com.example.demo.spring.pojo.User;
import org.junit.Test;
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
