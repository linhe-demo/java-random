package com.example.random.service;

import com.example.random.domain.common.exception.NewException;
import com.example.random.domain.common.support.ErrorCodeEnum;
import com.example.random.domain.constant.CommonEnum;
import com.example.random.domain.entity.LifeConfig;
import com.example.random.domain.entity.UserInfo;
import com.example.random.domain.repository.LifeConfigRepository;
import com.example.random.domain.repository.UserInfoRepository;
import com.example.random.domain.utils.BeanCopierUtil;
import com.example.random.domain.utils.MD5Util;
import com.example.random.domain.utils.TokenUtil;
import com.example.random.domain.utils.ToolsUtil;
import com.example.random.domain.value.RedisInfo;
import com.example.random.interfaces.client.LogClient;
import com.example.random.interfaces.client.vo.request.LogInfoRequest;
import com.example.random.interfaces.controller.put.request.life.LifeRequest;
import com.example.random.interfaces.controller.put.request.user.RegisterRequest;
import com.example.random.interfaces.controller.put.request.user.UserRequest;
import com.example.random.interfaces.controller.put.response.config.ConfigResponse;
import com.example.random.interfaces.controller.put.response.life.LifeResponse;
import com.example.random.interfaces.controller.put.response.user.RegisterResponse;
import com.example.random.interfaces.controller.put.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Lazy
@Service
@RequiredArgsConstructor
public class LifeMomentService {
    private final LifeConfigRepository lifeConfigRepository;
    private final UserInfoRepository userInfoRepository;
    private final RedissonClient redissonClient;
    private final LogClient logClient;

    public List<LifeResponse> momentData(LifeRequest request, HttpServletRequest ip) {
        List<LifeResponse> list = new ArrayList<>();
        List<LifeConfig> data;
        try {
            data = lifeConfigRepository.getLifeConfigData(request.getNum());
        } catch (NullPointerException e) {
            return list;
        }

        if (CollectionUtils.isEmpty(data)) {
            LifeResponse info = new LifeResponse();
            list.add(info);
        } else {
            data.forEach(i -> {
                LifeResponse info = new LifeResponse();
                info.setImgUrl(CommonEnum.IMAGE_FILE_PATH.getValue() + i.getImgUrl() + ".jpg");
                info.setText(i.getText());
                list.add(info);
            });
        }
        UserInfo user = TokenUtil.getCurrentUser();
        LogInfoRequest param = new LogInfoRequest();
        param.setAction("photo");
        param.setActionUser(Objects.requireNonNull(user).getUserName());
        param.setIp(ToolsUtil.getIp(ip));
        //记录日志
        logClient.saveLogInfo(param);
        return list;
    }

    public List<ConfigResponse> configData() {
        List<ConfigResponse> backData = new ArrayList<>();
        List<LifeConfig> data = lifeConfigRepository.getConfigData();
        data.forEach(i -> {
            ConfigResponse tmpData = new ConfigResponse();
            BeanCopierUtil.copy(i, tmpData);
            tmpData.setImgUrl(CommonEnum.IMAGE_FILE_PATH.getValue() + i.getImgUrl() + ".jpg");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            tmpData.setCreateTime(dateFormat.format(i.getCreateTime()));
            if (i.getUpdateTime() != null) {
                tmpData.setUpdateTime(dateFormat.format(i.getUpdateTime()));
            }
            backData.add(tmpData);
        });
        return backData;
    }

    public UserResponse checkLogin(UserRequest request, HttpServletRequest ip) {
        UserResponse backInfo = new UserResponse();
        UserInfo userInfo = userInfoRepository.findByUserName(request.getUserName());
        if (ObjectUtils.isEmpty(userInfo)) {
            throw new NewException(ErrorCodeEnum.USER_NOT_EXIST.getCode(), ErrorCodeEnum.USER_NOT_EXIST.getMsg());
        }

        if (!Objects.equals(userInfo.getStatus(), 1)) {
            throw new NewException(ErrorCodeEnum.ACCOUNT_IS_NOT_ACTIVATED.getCode(), ErrorCodeEnum.ACCOUNT_IS_NOT_ACTIVATED.getMsg());
        }

        LogInfoRequest param = new LogInfoRequest();
        param.setAction("login");
        param.setActionUser(request.getUserName());
        param.setIp(ToolsUtil.getIp(ip));
        //记录日志
        logClient.saveLogInfo(param);

        //检查账号密码是否正确
        if (Objects.equals(MD5Util.getMD5(request.getPassWord()), userInfo.getPassword())) {
            String token = TokenUtil.getToken(String.valueOf(userInfo.getId()), userInfo.getPassword());
            RedisInfo redisInfo = new RedisInfo();
            redisInfo.setId(userInfo.getId());
            redisInfo.setTime(new Date());
            redisInfo.setToken(token);
            redisInfo.setUserName(userInfo.getUserName());
            RBucket<String> bucket = redissonClient.getBucket(String.format("%s-linHeDemo", userInfo.getId()));
            bucket.set(ToolsUtil.convertToJson(redisInfo));
            backInfo.setExpiredTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + 86400 * 7);
            backInfo.setNickname(userInfo.getNickname());
            backInfo.setToken(token);
            //更新用户最近一次登录时间
            userInfoRepository.updateUserById(userInfo.getId());
        } else {
            throw new NewException(ErrorCodeEnum.WRONG_USER_PASSWORD.getCode(), ErrorCodeEnum.WRONG_USER_PASSWORD.getMsg());
        }
        return backInfo;
    }

    public RegisterResponse userRegister (RegisterRequest request, HttpServletRequest ip) {
        RegisterResponse backInfo = new RegisterResponse();
        UserInfo userInfo = userInfoRepository.findByUserName(request.getUserName());
        if (!ObjectUtils.isEmpty(userInfo)) {
            throw new NewException(ErrorCodeEnum.USER_ALREADY_EXISTS.getCode(), ErrorCodeEnum.USER_ALREADY_EXISTS.getMsg());
        }
        //保存用户账号信息
        userInfoRepository.saveUserInfo(request);
        //记录日志
        LogInfoRequest param = new LogInfoRequest();
        param.setAction("register");
        param.setActionUser(request.getUserName());
        param.setIp(ToolsUtil.getIp(ip));
        logClient.saveLogInfo(param);
        backInfo.setMsg(CommonEnum.REGISTRATION_SUCCESS.getValue());
        return backInfo;
    }
}
