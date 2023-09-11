package com.example.random.interfaces.controller.put.request.user;

import lombok.Data;

/**
 * 用户注册 请求参数类
 *
 * @author muhe
 * @since 2023-09-11
 */
@Data
public class RegisterRequest {
    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户密码
     */
    private String passWord;

    /**
     * 用户昵称
     */
    private String nickname;
}
