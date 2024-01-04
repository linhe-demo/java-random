package com.example.random.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://42.192.54.177:6379");
        singleServerConfig.setDatabase(0);
        singleServerConfig.setPassword("");
        singleServerConfig.setConnectionMinimumIdleSize(10);

        return Redisson.create(config);
    }

}
