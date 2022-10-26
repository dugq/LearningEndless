package com.example.demo.spring.pojo;

import com.example.demo.spring.pojo.annotation.MyAnnotation.*;
import org.hibernate.validator.constraints.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;


/**
 * Created by dugq on 2017/6/22.
 */
public class User implements Serializable{
    private static final long serialVersionUID = -7885061020439084967L;

    @Null(groups = INSERT.class,message = "已经存在不能新增~")
   /* @NotNull(message = "不许为null")
    @NotEmpty(message = "不许为空或者null")
    @NotBlank(message = "不许为null或者空包括空格回车tab等")*/
    private int id;
    @NotBlank(message = "姓名不能为空")
    @Length(min=1,max=20,message = "姓名必须控制在1个字符到20个字符之间")
    private String name;
    @NotNull(message = "性别不能为空")
    private Byte six;
    @Min(value = 0 ,groups = {AGE.class,NAME.class},message = "年龄必须在大于0")
    @Max(value = 100 ,groups = AGE.class,message = "年龄必须在小于100")
    @Range(min = 0,max = 100,message = "年龄必须在0-100")
    private Integer age;
    @Valid
    private User child;
    @AssertFalse(groups = MARRING.class,message = "必须是未婚才能结婚~")
    @AssertTrue(groups = MARRING.class,message = "必须是结婚才能离婚~")
    private boolean isMarry;
    @Past(message = "出生日期必须比今天早~")
    /*@Future(message="出生日期必须比今天晚~")*/
    private Date birthday;
    @Email(message = "请输出正确的邮编")
    private String email;
    @Pattern(regexp = "[A-Z]*")
    private String 大写字符;
    @CreditCardNumber(message = "请输出正确信用卡号")
    private String cardNumber;

    public User() {
    }

    public User(int id, String name, int six, Integer age) {
        this.id = id;
        this.name = name;
        this.six = (byte) six;
        this.age = age;
    }

    public boolean isMarry() {
        return isMarry;
    }

    public void setMarry(boolean marry) {
        isMarry = marry;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getSix() {
        return six;
    }

    public void setSix(Byte six) {
        this.six = six;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public User getChild() {
        return child;
    }

    public void setChild(User child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", six=" + six +
                ", age=" + age +
                ", child=" + child +
                '}';
    }

    public static void main(String[] args) {
        System.out.print(new Integer(1).toString());
    }

}

