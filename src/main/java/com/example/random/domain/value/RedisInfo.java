package com.example.random.domain.value;

import lombok.Data;

import java.util.Date;

/**
 * 写入redis 中的数据
 */
@Data
public class RedisInfo {
    /**
     * 当前时间
     */
    private Date time;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户id
     */
    private Integer Id;

    /**
     * 登录token
     */
    private String token;
}
