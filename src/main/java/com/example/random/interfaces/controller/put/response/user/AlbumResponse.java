package com.example.random.interfaces.controller.put.response.user;

import lombok.Data;

import java.util.Date;

/**
 * 相册列表 返回类
 *
 * @author muhe
 * @since 2023-09-18
 */
@Data
public class AlbumResponse {
    /**
     * 相册id
     */
    private Integer id;

    /**
     * 相册标题
     */
    private String title;

    /**
     * 相册描述
     */
    private String desc;

    /**
     * 相册时间
     */
    private String date;
}
