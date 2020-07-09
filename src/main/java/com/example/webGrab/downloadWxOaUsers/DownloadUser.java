package com.example.webGrab.downloadWxOaUsers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.util.CsvExportUtil;
import com.example.util.CsvReader;
import com.example.util.HttpClientUtils;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by dugq on 2019-07-19.
 */
public class DownloadUser {

    private static JSONObject downloadDate(UserBean lastEndUser){
        Map<String, String> params = new HashMap<>();
        params.put("action", "get_user_list");
        params.put("groupid", "-2");
        if(Objects.nonNull(lastEndUser)){
            params.put("begin_openid", lastEndUser.getUser_openid());
            params.put("begin_create_time", String.valueOf(lastEndUser.getUser_create_time()));
        }
        params.put("limit", "20");
        params.put("offset", "0");
        params.put("backfoward", "1");
        params.put("token", "505346062");
        params.put("lang", "zh_CN");
        params.put("f", "json");
        params.put("ajax", "1");
        params.put("random", "0.14638520829774904");
        return HttpClientUtils.httpGet("https://mp.weixin.qq.com/cgi-bin/user_tag", params);
    }


    private static List<UserBean> getUserList(UserBean lastEndUser){
        JSONObject jsonObject = downloadDate(lastEndUser);
        if(Objects.nonNull(jsonObject)){
            JSONObject user_list = jsonObject.getJSONObject("user_list");
            if(Objects.nonNull(user_list)){
                String userList = user_list.getString("user_info_list");
                if(Objects.nonNull(userList)){
                    return JSONArray.parseArray(userList,UserBean.class);
                }
            }
        }
        return Collections.emptyList();
    }

    private static void downloadAllUser(String pathname) throws InterruptedException, IOException {
        File file = new File(pathname);
        OutputStream out = new FileOutputStream(file);
        List<UserBean> userList = getUserList(null);
        List<UserBean> totalList = new ArrayList<>();
        int num = 0;
        while (CollectionUtils.isNotEmpty(userList)){
            System.err.println("追加 "+ userList.size() +" 总数 = " + (num = num+userList.size()));
            totalList.addAll(userList);
            userList = getUserList(userList.get(userList.size() - 1));
            Thread.sleep(1000);
        }
        CsvExportUtil.export(out,totalList, UserBean.class);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String pathname = "/Users/duguoqing/test/客集集-1.csv";
        downloadAllUser(pathname);
        return;
    }


}
