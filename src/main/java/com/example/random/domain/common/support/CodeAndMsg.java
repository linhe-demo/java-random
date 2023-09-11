package com.example.random.domain.common.support;

/**
 * 返回格式接口
 *
 * @author muhe
 * @since 2023-09-11
 */
public interface CodeAndMsg {
    /**
     * 返回 code
     *
     * @return string
     */
    Integer getCode();

    /**
     * 返回 信息
     *
     * @return string
     */
    String getMsg();
}
