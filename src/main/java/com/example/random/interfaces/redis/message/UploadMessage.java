package com.example.random.interfaces.redis.message;

import lombok.Data;

@Data
public class UploadMessage {
    private String path;

    private Long id;

    private Integer configId;

    private String action;
}
