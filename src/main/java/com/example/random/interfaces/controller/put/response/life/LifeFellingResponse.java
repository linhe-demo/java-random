package com.example.random.interfaces.controller.put.response.life;

import lombok.Data;

@Data
public class LifeFellingResponse {
    /**
     * 日期
     */
    private String date;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String text;
}
