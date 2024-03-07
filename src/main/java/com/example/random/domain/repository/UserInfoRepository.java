package com.example.random.domain.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.random.domain.entity.DateConfig;
import com.example.random.domain.entity.ExtensionData;
import com.example.random.domain.entity.UserBaby;
import com.example.random.domain.entity.UserInfo;
import com.example.random.interfaces.controller.put.request.user.RegisterRequest;

import java.util.List;

@DS("composer")
public interface UserInfoRepository {
    /**
     * 根据用户账号查询用户信息
     *
     * @param userName 用户账号
     * @return UserInfo
     */
    UserInfo findByUserName(String userName);

    /**
     * 根据用户id 获取用户信息
     *
     * @param uid int
     * @return UserInfo
     */
    UserInfo getById(Integer uid);

    /**
     * 保存用户账号信息
     *
     * @param request 用户注册信息
     */
    Long saveUserInfo(RegisterRequest request);

    /**
     * 根据用户ID 更新用户信息
     *
     * @param uid 用户uid
     */
    void updateUserById(UserInfo uid);

    /**
     * 检查用户孕育情况
     * @param id bigint
     * @return List<UserBaby>
     */
    UserBaby getBabyConfigById(long id);

    /**
     * 获取胎儿信息
     * @param weeks ExtensionData
     */
    ExtensionData findDetailById(int weeks);
}
