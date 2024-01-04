package com.example.random.config.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.random.annotation.PassToken;
import com.example.random.annotation.UserLoginToken;
import com.example.random.domain.common.exception.NewException;
import com.example.random.domain.common.support.ErrorCodeEnum;
import com.example.random.domain.entity.UserInfo;
import com.example.random.domain.repository.UserInfoRepository;
import com.example.random.domain.utils.ToolsUtil;
import com.example.random.domain.value.RedisInfo;
import com.example.random.interfaces.redis.producer.RedisQueue;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * token 验证器类
 *
 * @author muhe
 * @since 2023-09-11
 */

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    //注入service用于验证对象
    @Autowired
    private  UserInfoRepository userInfoRepository;
    @Autowired
    private RedisQueue redisQueue;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object object) {

        //在请求头中获取token
        String token = request.getHeader("Authorization");

        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();

        //检查是否有passToken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }

        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                // 执行认证
                if (token == null) {
                    throw new NewException(ErrorCodeEnum.TOKEN_CANNOT_BE_EMPTY.getCode(), ErrorCodeEnum.TOKEN_CANNOT_BE_EMPTY.getMsg());
                }
                // 获取 token 中的 user id
                String userId;
                try {
                    userId = JWT.decode(token).getAudience().get(0);
                } catch (JWTDecodeException j) {
                    throw new NewException(ErrorCodeEnum.TOKEN_PARSING_IS_INCORRECT.getCode(), ErrorCodeEnum.TOKEN_PARSING_IS_INCORRECT.getMsg());
                }
                UserInfo user = userInfoRepository.getById(Integer.valueOf(userId));
                if (user == null) {
                    throw new NewException(ErrorCodeEnum.USER_NOT_EXIST.getCode(), ErrorCodeEnum.USER_NOT_EXIST.getMsg());
                }

                //检查用户是token是否过期
                String redisToken = redisQueue.getValue(String.format("%s-linHeDemo", user.getId()));
                if (Objects.isNull(redisToken)) {
                    throw new NewException(ErrorCodeEnum.TOKEN_HAS_EXPIRED.getCode(), ErrorCodeEnum.TOKEN_HAS_EXPIRED.getMsg());
                }
                RedisInfo redisInfo = ToolsUtil.convertToObject(redisToken, RedisInfo.class);
                if (!Objects.equals(redisInfo.getToken(), token)) {
                    throw new NewException(ErrorCodeEnum.TOKEN_PARSING_IS_INCORRECT.getCode(), ErrorCodeEnum.TOKEN_PARSING_IS_INCORRECT.getMsg());
                }

                // 验证 token
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
                try {
                    jwtVerifier.verify(token);
                } catch (Exception e) {
                    throw new NewException(ErrorCodeEnum.USER_TOKEN_IS_INCORRECT.getCode(), ErrorCodeEnum.USER_TOKEN_IS_INCORRECT.getMsg());
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest httpServletRequest,
                           @NotNull HttpServletResponse httpServletResponse,
                           @NotNull Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest httpServletRequest,
                                @NotNull HttpServletResponse httpServletResponse,
                                @NotNull Object o, Exception e) throws Exception {
    }
}
