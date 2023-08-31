package com.example.random.interfaces.controller.put.response.config;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 生活配置 信息返回类
 *
 * @author muhe
 * @since 2023-08-31
 */
@Data
public class ConfigResponse {
    private Integer id;

    private String imgUrl;

    private String text;

    private Integer status;
    private String CreateTime;
    private String UpdateTime;
}
