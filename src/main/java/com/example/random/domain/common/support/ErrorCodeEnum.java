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
public enum ErrorCodeEnum implements CodeAndMsg {

    USER_NOT_EXIST(10001, "用户不存在"),
    WRONG_USER_PASSWORD(10002, "用户密码错误"),
    TOKEN_CANNOT_BE_EMPTY(10003, "token 不能为空"),
    TOKEN_PARSING_IS_INCORRECT(10004, "token 解析异常"),
    USER_TOKEN_IS_INCORRECT(10005, "用户token 验证失败"),
    USER_ALREADY_EXISTS(10006, "用户已存在！"),
    TOKEN_HAS_EXPIRED(10007, "token 已过期")
    ;
    private Integer code;
    private String msg;

    ErrorCodeEnum(Integer code, String msg) {
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
