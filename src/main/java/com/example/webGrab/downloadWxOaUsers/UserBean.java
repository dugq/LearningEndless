package com.example.webGrab.downloadWxOaUsers;

import com.example.util.CsvFiled;
import lombok.Data;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dugq on 2019-07-19.
 */
@Data
public class UserBean implements Serializable {

    private static final long serialVersionUID = -4666368492054807261L;


    @CsvFiled("open ID")
    private String user_openid;
    @CsvFiled("昵称")
    private String user_name;
    private long user_create_time;

    @CsvFiled("关注时间")
    private String create_time;

    @CsvFiled("头像地址")
    private String user_head_img;
    @CsvFiled("备注")
    private String user_remark;

    public String getCreate_time() {
        return DateFormatUtils.format(new Date(user_create_time*1000),"yyyy-MM-dd HH:mm:ss");
    }
}
