package com.example.random.interfaces.mq.message;

import lombok.Data;

@Data
public class UploadImgMessage {
    /**
     * 路径
     */
    private String path;
    /**
     * id
     */
    private int id;
}
