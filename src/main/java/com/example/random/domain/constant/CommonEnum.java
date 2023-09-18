package com.example.random.domain.constant;

import lombok.Getter;

@Getter
public enum CommonEnum {
    IMAGE_FILE_PATH("http://www.life-moment.top/images/life/"),
    REGISTRATION_SUCCESS("注册成功， 请使用注册的账号登录");
    private final String value;

    CommonEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
