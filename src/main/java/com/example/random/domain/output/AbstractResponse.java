package com.example.random.domain.output;

import com.example.random.domain.constant.CommonCode;
import lombok.Getter;

@Getter
public class AbstractResponse {

    /**
     * 返回码
     */
    protected Integer code = CommonCode.SUCCESS.getCode();

    /**
     * 对返回码的文本描述内容
     */
    protected String msg = CommonCode.SUCCESS.getMsg();

}