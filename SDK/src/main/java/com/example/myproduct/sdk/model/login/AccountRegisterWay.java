package com.example.myproduct.sdk.model.login;

/**
 * 账户注册方式
 *
 * @author lihanguang
 * @date 2017/4/14 20:15:05
 */
public enum AccountRegisterWay {
    STR(111, "任意字符串(如邮箱,昵称)作为账号来进行注册的方式"),
    PHONE_NUM(222, "手机号码作为账号来进行注册的方式");

    private int code;
    private String description;

    AccountRegisterWay(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
