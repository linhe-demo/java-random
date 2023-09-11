package com.example.random.config;

import com.example.random.config.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 拦截器类
 *
 * @author muhe
 * @since 2023-09-11
 */

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {
    @Autowired
    JwtInterceptor jwtInterceptor;

    //增加拦截的规则
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)//将自己写好的拦截类放进来
                .addPathPatterns("/**")//拦截所有
                .excludePathPatterns("/api/v1/user/login", "/api/v1/user/register");//这两个放行
    }
}
