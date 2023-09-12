package com.example.random.config.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.random.domain.common.exception.NewException;
import com.example.random.domain.common.support.ErrorCodeEnum;
import com.example.random.domain.entity.UserInfo;
import com.example.random.domain.repository.UserInfoRepository;
import com.example.random.domain.utils.ToolsUtil;
import com.example.random.domain.value.RedisInfo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * token 验证器类
 *
 * @author muhe
 * @since 2023-09-11
 */

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    //注入service用于验证对象
    private final UserInfoRepository userInfoRepository;
    private final RedissonClient redissonClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //在请求头中获取token
        String token = request.getHeader("Authorization");

        //若为空则抛出异常
        if (StringUtils.isEmpty(token)) {
            throw new NewException(ErrorCodeEnum.TOKEN_CANNOT_BE_EMPTY.getCode(), ErrorCodeEnum.TOKEN_CANNOT_BE_EMPTY.getMsg());
        }

        //获取token中的id
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (Exception e) {
            throw new NewException(ErrorCodeEnum.TOKEN_PARSING_IS_INCORRECT.getCode(), ErrorCodeEnum.TOKEN_PARSING_IS_INCORRECT.getMsg());
        }

        //判断该id的用户是否存在
        UserInfo user = userInfoRepository.getById(Integer.valueOf(userId));
        if (ObjectUtils.isEmpty(user)) {
            throw new NewException(ErrorCodeEnum.USER_NOT_EXIST.getCode(), ErrorCodeEnum.USER_NOT_EXIST.getMsg());
        }

        //检查用户是token是否过期
        String redisToken = (String) redissonClient.getBucket(String.format("%s-linHeDemo", user.getId())).get();
        RedisInfo redisInfo = ToolsUtil.convertToObject(redisToken, RedisInfo.class);
        if (!Objects.equals(redisInfo.getToken(), token)) {
            throw new NewException(ErrorCodeEnum.TOKEN_HAS_EXPIRED.getCode(), ErrorCodeEnum.TOKEN_HAS_EXPIRED.getMsg());
        }
        //核心：进行验证
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (Exception e) {
            throw new NewException(ErrorCodeEnum.USER_TOKEN_IS_INCORRECT.getCode(), ErrorCodeEnum.USER_TOKEN_IS_INCORRECT.getMsg());
        }

        //没问题返回true
        return true;
    }

}
