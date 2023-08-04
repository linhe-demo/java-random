package com.example.random.interfaces.client.vo.request;

import lombok.Data;

/**
 * 日志信息 请求参数
 *
 * @author muhe
 * @date 2023-08-03
 */
@Data
public class LogInfoRequest {
    /**
     * 操作类型
     */
    private String action;
    /**
     * 操作人
     */
    private String actionUser;
}
