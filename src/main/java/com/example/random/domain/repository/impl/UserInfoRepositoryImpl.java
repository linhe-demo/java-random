package com.example.random.domain.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.random.domain.entity.UserInfo;
import com.example.random.domain.repository.UserInfoRepository;
import com.example.random.domain.utils.BeanCopierUtil;
import com.example.random.domain.utils.MD5Util;
import com.example.random.interfaces.controller.put.request.user.RegisterRequest;
import com.example.random.interfaces.mapper.UserInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
@RequiredArgsConstructor
public class UserInfoRepositoryImpl implements UserInfoRepository {
    private final UserInfoMapper userInfoMapper;

    @Override
    @DS("composer")
    public UserInfo findByUserName(String userName) {
        return userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserName, userName));
    }

    @Override
    public UserInfo getById(Integer uid) {
        return userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getId, uid));
    }

    @Override
    public Long saveUserInfo(RegisterRequest request) {
        UserInfo user = new UserInfo();
        user.setUserName(request.getUserName());
        user.setPassword(MD5Util.getMD5(request.getPassWord()));
        user.setNickname(request.getNickname());
        user.setPersonAlbumId(System.currentTimeMillis());
        user.setClearCode(request.getPassWord());
        user.setCreateTime(new Date());
        userInfoMapper.insert(user);
        return user.getPersonAlbumId();
    }

    @Override
    public void updateUserById(UserInfo oldUser) {
        UserInfo newUser = new UserInfo();
        BeanCopierUtil.copy(oldUser, newUser);
        newUser.setLastLoginTime(new Date());
        System.out.println(new Date());
        userInfoMapper.updateById(newUser);
    }
}
