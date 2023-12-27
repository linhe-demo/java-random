package com.example.random.interfaces.redis.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisQueue {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 发送订阅消息
     *
     * @param message String
     */
    public void push(String message) {
        redisTemplate.convertAndSend("LinHe-demo-queue", message);
    }

    /**
     * 存储信息
     *
     * @param key        消息键名
     * @param message    消息内容
     * @param expiration 过期时间 当为0 时 为永久键
     */
    public void setValue(String key, String message, long expiration) {
        redisTemplate.opsForValue().set(key, message);
        if (expiration > 0) {
            redisTemplate.expire(key, expiration, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取信息
     *
     * @param key 消息键名
     * @return String
     */
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除信息
     *
     * @param key 消息键名
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
