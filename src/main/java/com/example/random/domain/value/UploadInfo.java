package com.example.random.domain.value;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadInfo {
    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 配置id
     */
    private String configId;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 文件列表
     */
    private MultipartFile[] files;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String desc;

    /**
     * 日期
     */
    private String date;
}
