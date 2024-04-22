package com.example.random.interfaces.controller.put.response.user;

import lombok.Data;

@Data
public class UserImageResponse {
    private String src;
    private Boolean loaded;

    public UserImageResponse(String src, boolean loaded) {
        this.src = src;
        this.loaded = loaded;
    }
}
