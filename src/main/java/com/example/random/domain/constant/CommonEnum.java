package com.example.random.domain.constant;

import lombok.Getter;

@Getter
public enum CommonEnum {
    IMAGE_FILE_PATH("http://150.158.82.218/images/"),
    REGISTRATION_SUCCESS("注册成功");
    private final String value;

    CommonEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}