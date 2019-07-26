package com.example.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dugq on 2017/10/17.
 */
public class HttpClientUtils {
    // utf-8字符编码
    public static final String CHARSET_UTF_8 = "utf-8";
    public static final String CONTENT_TYPE_JSON_URL = "application/json;charset=utf-8";
    /**
     *
     */
    private static CloseableHttpClient client;
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000).setConnectionRequestTimeout(1000)
            .setSocketTimeout(5000).build();

    private static CloseableHttpClient getClient(){
        if(client == null){
            synchronized (HttpClientBuilder.class){
                if(client == null){
                    synchronized (HttpClientBuilder.class){
                        client = HttpClientBuilder.create().build();
                    }
                }
            }
        }
        return client;
    }

    private static synchronized void closeClinet (){
        if(client!=null){
            try {
                client.close();
                client = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get
     *
     * @return
     */
    public static JSONObject httpGet(String url) {
        HttpGet get = new HttpGet(url);
        get.setHeader("Cookie","ua_id=VAjM9w5k4qt9ZKGKAAAAAMp9ltGJ6kNCFryDIffEl9k=; ts_uid=5491048380; noticeLoginFlag=1; wxuin=55471148753672; mm_lang=zh_CN; pgv_pvi=7419262976; pgv_pvid=7181212668; rewardsn=; wxtokenkey=777; pgv_si=s6207561728; cert=SrnHbhUaRICsCqAbp6BwVZZx9rKok_87; pgv_info=ssid=s35641142; verifysession=h01b4981037cb541910065840341dbb6332068a83bb24f26e683e14d604c46434279cf301d14eee342b; qm_authimgs_id=1; qm_verifyimagesession=h014bb74d6897c06ebcf6afaa541c7ab469c338d7bb8074a96b62daae81cfbfd3752b746c8e0e02976b; openid2ticket_oxYAN5M1zsrWvPerhQRoGSzlaZZ0=VCqCcZPharyKVIqm1VtijmR1tei04Cc8L4rPhN+CJJc=; openid2ticket_oE34T1dgHYxjeMyE6Ppcw8BoyMRY=VctfeAoJykj3grc95IfWNGphDtllf9oMSSIP3ByupT4=; uuid=82814c14614339726c79e8b21db59c44; bizuin=3594932505; ticket=2fb79fc4d0fe0ff914702ea8481028a2bc0682d6; ticket_id=gh_4ef9ffc28d7f; remember_acct=biexiangling%40duiba.com.cn; data_bizuin=3594932505; data_ticket=sRw6KVehOYZGPqBNh4lL5pPwAvJ8Imnv0s7ag5cVM4uCgGCjT7RlBP3rimhdxcib; slave_sid=TE16bXlwa2pXZ2FSX1daQ21uYnZfSUlrR2s2T21CelF5clpOdEVIeUtlMmR3N0NiaU5KXzM1YmhsQ0twVEdxWDVvcDUzQVhRVUgzMzFTNzVVQThvblVLVmNnTW5pZnlfT2dKbzVhbWE4bXFUeF9LWXlGVzMybVFLSTlsOWdDcFAzaVVYR2ptUHcwTjB4WnhC; slave_user=gh_4ef9ffc28d7f; xid=e389f807b1f59b1919c19b4a20679f21; openid2ticket_oGVFG1vgtn1eP5PHo5orJS6Whzo0=7dxA9Ar7hLhSLsUFy5LAgLjNh2ocgwQdLm29h/MTlXM=");
        get.setConfig(requestConfig);
        try {
            CloseableHttpResponse execute = executeRequest(get);
            return readResponse(execute);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 重债get
     * @param url
     * @param params
     * @return
     */
    public static JSONObject httpGet(String url, Map<String, String> params) {
        if (!CollectionUtils.isEmpty(params)) {
            if (url.indexOf("?") == -1) {
                url += "?";
            }
            final StringBuilder sb = new StringBuilder(url);
            params.forEach((key, value) -> sb.append(key).append("=").append(value).append("&"));
            sb.substring(0, sb.lastIndexOf("&"));
            url = sb.toString();
        }
        return httpGet(url);
    }


    /**
     * 发送post
     *
     * @param url
     * @param params
     * @return
     */
    public static JSONObject httpPost(String url, Map<String, String> params) {
        String jsonString = JSON.toJSONString(params);
        return httpPostJSON(url, jsonString);
    }

    /**
     * post重载方法
     *
     * @param url
     * @param params 严格json格式的字符串
     * @return
     */
    public static JSONObject httpPostJSON(String url, String params) {
        //定义post请求
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);
        JSONObject result;
        try {
            if (!StringUtils.isBlank(params)) {
                StringEntity stringEntity = new StringEntity(params, "UTF-8");
                stringEntity.setContentType(CONTENT_TYPE_JSON_URL);
                post.setEntity(stringEntity);
            }
            CloseableHttpResponse execute = executeRequest(post);
            return readResponse(execute);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 上传文件
     *
     * @param url
     * @param file   spring MVC接收的file对象
     * @param params 需要的附带参数
     * @return
     */
    public static JSONObject httpPostFile(String url, MultipartFile file, Map<String, String> params) {
        //定义post请求
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);
        try {
            //定义multipart请求格式。用于上传文件的请求
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            //设置为浏览器兼容模式
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            //一下将基本参数填入multipart
            String originalFilename = file.getOriginalFilename();
            String fileType = originalFilename.substring(originalFilename.indexOf(".") + 1,
                    originalFilename.length());
            multipartEntityBuilder.addBinaryBody("file", file.getInputStream(),
                    ContentType.APPLICATION_OCTET_STREAM, file.getOriginalFilename() + "." + fileType);
            params.forEach((key, value) -> multipartEntityBuilder.addTextBody(key, value, ContentType.TEXT_PLAIN));
            //定义 ：http message entity 用于http请求传输的实体类
            //An entity that can be sent or received with an HTTP message.
            HttpEntity entity = multipartEntityBuilder.build();
            post.setEntity(entity);
            JSONObject result;
            //发送http请求，并获取到请求结果
            CloseableHttpResponse response = executeRequest(post);
            result = readResponse(response);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static CloseableHttpResponse executeRequest(HttpUriRequest post) throws IOException {
        CloseableHttpClient client = getClient();
        if(client==null){
            return null;
        }
        synchronized (client){
            try {
                return client.execute(post);
            }catch (ConnectionPoolTimeoutException e){
                closeClinet();
                return null;
            }
        }
    }

    /**
     * 解析response返回JSONObject
     * @param response
     * @return
     * @throws IOException
     */
    private static JSONObject readResponse(CloseableHttpResponse response) throws IOException {
        if(Objects.isNull(response)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code","-1");
            jsonObject.put("content","连接失败，请稍后重试~");
            return jsonObject;
        }
        JSONObject result;
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String value = readResponseOutputStream(response);
            try {
                result = JSON.parseObject(value);
            } catch (Exception e) {
                result = new JSONObject();
                result.put("code", "-1");
                result.put("content", value);
            }
        } else {
            result = new JSONObject();
            result.put("code", response.getStatusLine().getStatusCode());
//            result.put("content", readResponseOutputStream(response));
        }
        return result;
    }

    /**
     * 解析Response的body，读取返回值。 注意读取结束时已将response 关闭。 将不可使用
     * @param response
     * @return
     */
    private static String readResponseOutputStream(CloseableHttpResponse response) {
        HttpEntity resEntity = response.getEntity();
        try {
           return EntityUtils.toString(resEntity, CHARSET_UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return readResponseOutputStreamSimple(response);
        } finally {
            // 释放资源
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private static String readResponseOutputStreamSimple(CloseableHttpResponse response) {
        HttpEntity resEntity = response.getEntity();
        StringBuffer strBuf = new StringBuffer("");
        InputStream is = null;
        try {
            is = resEntity.getContent();
            byte[] buffer = new byte[1024];
            int r = 0;
            while ((r = is.read(buffer)) > 0) {
                strBuf.append(new String(buffer, 0, r, "UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strBuf.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        closeClinet();
        super.finalize();
    }

    public static void main(String[] args) {
//        JSONObject jsonObject = httpPostJSON("http://zjdev.fingercrm.cn/zjqd-web/channels/sp/rebate/getRebateDetaiList.do", "{\"accesstoken\":\"696608afe24c35d6f1767ca209813442\", \"curPage\":1,\"pageSize\":20,\"publicId\":\"wx4f8c668abdd46305\",\"searchParam\":\"\",\"time\":\"1508143210697\"}");
        JSONObject jsonObject = httpPostJSON("http://zjdev.fingercrm.cn/zjqd-web/channels/sp/query/rebate/PublicAccountzRemain",
                "{\"publicId\":\"gh_155170e9f6b2\",\"curPage\":1,\"pageSize\":20}");
        System.out.println(jsonObject);
    }
}
