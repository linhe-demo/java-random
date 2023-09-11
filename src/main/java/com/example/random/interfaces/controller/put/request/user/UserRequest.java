package com.example.random.interfaces.controller.put.request.user;

import lombok.Data;

/**
 * 用户登录 请求参数类
 *
 * @author muhe
 * @since 2023-09-05
 */
@Data
public class UserRequest {
    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户密码
     */
    private String passWord;
}
