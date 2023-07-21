package com.example.random.interfaces.controller.put.response.life;

import lombok.Data;

/**
 * 生活配置返回类
 *
 * @author muhe
 * @date 2023-07-21
 */
@Data
public class LifeResponse {
    /**
     * 图片路由
     */
    private String imgUrl;
    /**
     * 文案
     */
    private String text;
}
