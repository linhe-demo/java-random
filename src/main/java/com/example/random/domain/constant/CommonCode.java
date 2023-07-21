package com.example.random.domain.constant;

import com.example.random.domain.constant.impl.CodeAndMsg;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommonCode implements CodeAndMsg {
    SUCCESS(200, "SUCCESS"),
    ;
    /**
     * 返回状态码
     */
    private Integer code;
    /**
     * 返回信息
     */
    private String msg;

    CommonCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
