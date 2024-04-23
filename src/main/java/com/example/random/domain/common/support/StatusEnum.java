package com.example.random.domain.common.support;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 返回数据格式枚举
 *
 * @author muhe
 * @since 2023-09-11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum StatusEnum implements CodeAndMsg {

    STATUS_ON(2, "启用"),
    STATUS_OFF(1, "未启用"),
    USER_ON(1, "启用"),
    USER_OFF(0, "未启用"),
    BABY_ON(1, "出生"),
    BABY_OFF(2, "未出生"),
    MALE(1, "爸爸"),
    FEMALE(2, "妈妈"),
    IMAGE_NUM(6, "主页个人岁月照片展示数量");


    private Integer code;
    private String msg;

    StatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
