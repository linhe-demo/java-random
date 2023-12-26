package com.example.random.interfaces.redis.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisQueue {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void push(String message) {
        redisTemplate.convertAndSend("LinHe-demo-queue", message);
    }
}
