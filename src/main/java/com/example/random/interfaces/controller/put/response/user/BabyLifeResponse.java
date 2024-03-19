package com.example.random.interfaces.controller.put.response.user;

import lombok.Data;

@Data
public class BabyLifeResponse {
    /**
     * id
     */
    private Integer id;

    /**
     * 日期
     */
    private String date;

    /**
     * 标题
     */
    private String title;

    /**
     * 介绍
     */
    private String text;
}
