package basic;

import com.example.DemoApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by dugq on 2017/7/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = DemoApplication.class)
public abstract class ControllerBasicTest {
    protected static final Logger LOG = LoggerFactory.getLogger(ControllerBasicTest.class);

    @Autowired
    protected WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;

    /**
     *在junit之前利用WebApplicationContext初始化mockMvc
     */
    @Before
    public void setupMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * 发送get请求并断言请求返回状态为200
     * @param urlTemplate   url
     * @return   返回ResultActions  用于继续断言 以及获得responseBody
     * @throws Exception  抛出的异常代表断言失败~
     */
    protected ResultActions get(String urlTemplate) throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(urlTemplate)).andExpect(MockMvcResultMatchers.status().isOk());
        return resultActions;
    }

    /**
     * 发送post请求并断言请求返回状态为200
     * @param urlTemplate  url
     * @param param  请求参数，例如表单提交，先用对象封装 作为参数提交，本方法将object 转化为 json 提交
     * @return 返回ResultActions  用于继续断言 以及获得responseBody
     * @throws Exception 抛出的异常代表断言失败~
     */
    protected ResultActions postJSON(String urlTemplate,Object param) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(param);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate).contentType(MediaType.APPLICATION_JSON).content(requestJson)).andExpect(MockMvcResultMatchers.status().isOk());
        return resultActions;
    }

    /**
     * 重载 用于无参post请求
     * @param urlTemplate  url
     * @return
     * @throws Exception
     */
    protected ResultActions post(String urlTemplate) throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(urlTemplate)).andExpect(MockMvcResultMatchers.status().isOk());
        return resultActions;
    }

    /**
     * 重载 用于参数无法用Object组装，则使用map 存储键值对
     * @param urlTemplate url
     * @return
     * @throws Exception
     */
    protected ResultActions post(String urlTemplate, Map<String,String> params) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(urlTemplate).contentType(MediaType.APPLICATION_JSON);
        for(Map.Entry entry : params.entrySet()){
            requestBuilder.param((String)entry.getKey(),(String)entry.getValue());
        }
        ResultActions resultActions = mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
        return resultActions;
    }
    /**
     * 输出结果
     * @param perform  ResultActions对象
     * @throws UnsupportedEncodingException
     */
    protected void printResponse(ResultActions perform) throws UnsupportedEncodingException {
        String contentAsString = perform.andReturn().getResponse().getContentAsString();
        LOG.error(contentAsString);
    }
}
