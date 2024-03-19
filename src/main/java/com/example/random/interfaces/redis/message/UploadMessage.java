package com.example.random.interfaces.redis.message;

import lombok.Data;

@Data
public class UploadMessage {
    private String path;

    private Long id;

    private String configId;

    private String action;

    private String name;

    private String desc;

    private String date;
}
