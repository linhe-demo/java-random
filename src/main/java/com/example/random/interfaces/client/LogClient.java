package com.example.random.interfaces.client;

import com.example.random.interfaces.client.vo.request.ImageDeleteRequest;
import com.example.random.interfaces.client.vo.request.LogInfoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 日志 接口
 *
 * @author muhe
 * @date 2023-08-02
 */
@FeignClient(name = "LogClient", url = "http://139.196.50.102:9999/")
public interface LogClient {
    @PostMapping(path = "log/save", consumes = "application/json")
    String saveLogInfo(@RequestBody LogInfoRequest request);

    @PostMapping(path = "image/delete", consumes = "application/json")
    String deleteImage(@RequestBody ImageDeleteRequest request);
}


