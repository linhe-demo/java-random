package com.example.random.interfaces.controller.put.request.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BabyLifeRequest {
    /**
     * 上传的文件
     */
    private MultipartFile[] files;

    /**
     * 记录 title
     */
    private String name;

    /**
     * 记录 描述
     */
    private String desc;

    /**
     * 记录时间
     */
    private String date;
}
