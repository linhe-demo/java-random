package com.example.random.interfaces.controller.put.response.user;

import lombok.Data;

/**
 * 用户登录 返回信息类
 *
 * @author muhe
 * @since 2023-09-05
 */
@Data
public class UserResponse {
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户token
     */
    private String Token;
    /**
     * 登录过期时间
     */
    private Long expiredTime;
}
