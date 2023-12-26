package com.example.random.domain.utils;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.random.domain.entity.UserInfo;
import com.example.random.domain.repository.UserInfoRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

/**
 * token 专用工具
 *
 * @author muhe
 * @since 2023-09-11
 */
@Component
public class TokenUtil {
    private static UserInfoRepository staticUserInfoRepository;

    @Resource
    private  UserInfoRepository userInfoRepository;

    @PostConstruct
    public void setLifeMomentService() {
        staticUserInfoRepository = userInfoRepository;
    }

    public static String getToken(String userId, String password) {
        return JWT.create().withAudience(userId)
                .withExpiresAt(DateUtil.offsetHour(new Date(), 168))
                .sign(Algorithm.HMAC256(password));
    }


    /**
     * 获取当前登录的用户信息
     *
     * @return user对象
     */
    public static UserInfo getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String token = request.getHeader("Authorization");
            if (StringUtils.isNotEmpty(token)) {
                String userId = JWT.decode(token).getAudience().get(0);
                return staticUserInfoRepository.getById(Integer.valueOf(userId));
            }
        } catch (Exception e) {
            return null;
        }
        return new UserInfo();
    }
}

