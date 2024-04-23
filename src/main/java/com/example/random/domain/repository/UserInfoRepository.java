package com.example.random.domain.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.random.domain.entity.*;
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
    List<UserBaby> getBabyConfigById(long id);

    /**
     * 获取胎儿信息
     * @param weeks ExtensionData
     */
    ExtensionData findDetailById(int weeks);

    /**
     * 保存宝宝成长记录
     * @param personAlbumId Long
     * @param name String
     * @param desc String
     * @param date String
     * @return Integer
     */
    Integer saveBabyLifeInfo(Long personAlbumId, String name, String desc, String date);

    /**
     * 获取baby lie list
     * @param personAlbumId long
     * @return List<BabyLifeData>
     */
    List<BabyLifeData> getBabyLifeInfo(Long personAlbumId);

    /**
     * 获取用户配置
     * @param personAlbumId Long
     * @return  UserConfig
     */
    UserConfig getUserConfigByAlbumId(Long personAlbumId);
}
