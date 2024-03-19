package com.example.random.domain.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.random.domain.common.support.StatusEnum;
import com.example.random.domain.entity.BabyLifeData;
import com.example.random.domain.entity.ExtensionData;
import com.example.random.domain.entity.UserBaby;
import com.example.random.domain.entity.UserInfo;
import com.example.random.domain.repository.UserInfoRepository;
import com.example.random.domain.utils.BeanCopierUtil;
import com.example.random.domain.utils.MD5Util;
import com.example.random.domain.utils.ToolsUtil;
import com.example.random.interfaces.controller.put.request.user.RegisterRequest;
import com.example.random.interfaces.mapper.BabyLifeDataMapper;
import com.example.random.interfaces.mapper.ExtensionDataMapper;
import com.example.random.interfaces.mapper.UserBabyMapper;
import com.example.random.interfaces.mapper.UserInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserInfoRepositoryImpl implements UserInfoRepository {
    private final UserInfoMapper userInfoMapper;
    private final UserBabyMapper userBabyMapper;
    private final ExtensionDataMapper extensionDataMapper;
    private final BabyLifeDataMapper babyLifeDataMapper;


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
        user.setStatus(StatusEnum.USER_OFF.getCode());
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
        userInfoMapper.updateById(newUser);
    }

    @Override
    public List<UserBaby> getBabyConfigById(long id) {
        return userBabyMapper.selectList(Wrappers.<UserBaby>lambdaQuery()
                .eq(UserBaby::getPersonAlbumId, id)
        );
    }

    @Override
    @DS("life")
    public ExtensionData findDetailById(int weeks) {
        return extensionDataMapper.selectOne(Wrappers.<ExtensionData>lambdaQuery()
                .eq(ExtensionData::getId, weeks)
        );
    }

    @Override
    @DS("life")
    public Integer saveBabyLifeInfo(Long personAlbumId, String name, String desc, String date) {
        BabyLifeData info = new BabyLifeData();
        info.setTitle(name);
        info.setText(desc);
        info.setPersonAlbumId(personAlbumId);
        info.setDate(ToolsUtil.StringToDate(date));
        info.setCreateAt(new Date());
        babyLifeDataMapper.insert(info);
        return info.getId();
    }

    @Override
    @DS("life")
    public List<BabyLifeData> getBabyLifeInfo(Long personAlbumId) {
        return babyLifeDataMapper.selectList(Wrappers.<BabyLifeData>lambdaQuery()
                .eq(BabyLifeData::getPersonAlbumId, personAlbumId)
                .orderByAsc(BabyLifeData::getDate));
    }
}
