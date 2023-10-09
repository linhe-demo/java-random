package com.example.random.service;

import com.example.random.domain.common.exception.NewException;
import com.example.random.domain.common.support.ErrorCodeEnum;
import com.example.random.domain.entity.AlbumConfig;
import com.example.random.domain.entity.LifeConfig;
import com.example.random.domain.entity.UserInfo;
import com.example.random.domain.repository.AlbumConfigRepository;
import com.example.random.domain.repository.LifeConfigRepository;
import com.example.random.domain.repository.UserInfoRepository;
import com.example.random.domain.utils.*;
import com.example.random.domain.value.RedisInfo;
import com.example.random.interfaces.client.LogClient;
import com.example.random.interfaces.client.vo.request.LogInfoRequest;
import com.example.random.interfaces.client.vo.response.ImageInfoResponse;
import com.example.random.interfaces.controller.put.request.life.LifeRequest;
import com.example.random.interfaces.controller.put.request.user.RegisterRequest;
import com.example.random.interfaces.controller.put.request.user.UserRequest;
import com.example.random.interfaces.controller.put.response.config.ConfigResponse;
import com.example.random.interfaces.controller.put.response.life.LifeResponse;
import com.example.random.interfaces.controller.put.response.user.AlbumResponse;
import com.example.random.interfaces.controller.put.response.user.RegisterResponse;
import com.example.random.interfaces.controller.put.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

import okhttp3.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Lazy
@Service
@RequiredArgsConstructor
public class LifeMomentService {
    private final LifeConfigRepository lifeConfigRepository;
    private final UserInfoRepository userInfoRepository;
    private final AlbumConfigRepository albumConfigRepository;
    private final RedissonClient redissonClient;
    private final LogClient logClient;

    public List<LifeResponse> momentData(LifeRequest request, HttpServletRequest ip) {
        List<LifeResponse> list = new ArrayList<>();
        List<LifeConfig> data;
        try {
            data = lifeConfigRepository.getLifeConfigData(request.getId());
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }

        if (CollectionUtils.isEmpty(data)) {
            return new ArrayList<>();
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
            userInfoRepository.updateUserById(userInfo);
        } else {
            throw new NewException(ErrorCodeEnum.WRONG_USER_PASSWORD.getCode(), ErrorCodeEnum.WRONG_USER_PASSWORD.getMsg());
        }
        return backInfo;
    }

    public RegisterResponse userRegister(RegisterRequest request, HttpServletRequest ip) {
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
        backInfo.setMsg(ErrorCodeEnum.REGISTRATION_SUCCESS.getMsg());
        return backInfo;
    }

    public List<AlbumResponse> getAlbumList(HttpServletRequest ip) {
        List<AlbumResponse> backInfo = new ArrayList<>();
        List<AlbumConfig> list = albumConfigRepository.getAlbumConfig();
        final Integer[] num = {1};
        list.forEach(i -> {
            AlbumResponse albumResponse = new AlbumResponse();
            BeanCopierUtil.copy(i, albumResponse);
            albumResponse.setDate(ToolsUtil.convertTimestampToStandardFormatDay(i.getDate().getTime()));
            albumResponse.setTheme(String.format("right theme-%d", num[0] % 4));
            backInfo.add(albumResponse);
            num[0]++;
        });
        UserInfo user = TokenUtil.getCurrentUser();
        LogInfoRequest param = new LogInfoRequest();
        param.setAction("album-list");
        param.setActionUser(Objects.requireNonNull(user).getUserName());
        param.setIp(ToolsUtil.getIp(ip));
        //记录日志
        logClient.saveLogInfo(param);
        return backInfo;
    }

    public Boolean autoLogin(HttpServletRequest ip) {
        UserInfo user = TokenUtil.getCurrentUser();
        //检查缓存中是否存在用户登录信息
        RBucket<String> bucket = redissonClient.getBucket(String.format("%s-linHeDemo", Objects.requireNonNull(user).getId()));

        LogInfoRequest param = new LogInfoRequest();
        param.setAction("auto-login");
        param.setActionUser(Objects.requireNonNull(user).getUserName());
        param.setIp(ToolsUtil.getIp(ip));
        //记录日志
        logClient.saveLogInfo(param);

        return Objects.nonNull(bucket);
    }

    public Boolean upload(MultipartFile[] files, HttpServletRequest ip) {
        System.out.println(Arrays.toString(files));
        if (files == null || ObjectUtils.isEmpty(files)) {
            throw new NewException(ErrorCodeEnum.FILE_IS_EMPTY.getCode(), ErrorCodeEnum.FILE_IS_EMPTY.getMsg());
        }
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            try {
                //保存文件到本地
                File mkdir = new File("images\\tmp");
                if (!mkdir.exists()) {
                    mkdir.mkdirs();
                }
                long id = SnowflakeUtil.generateId();
                String filePath = String.format("%s\\%s-%s", mkdir.getPath(), id, fileName);
                //定义输出流，将文件写入硬盘
                FileOutputStream os = new FileOutputStream(filePath);
                InputStream in = file.getInputStream();
                int b = 0;
                while ((b = in.read()) != -1) { //读取文件
                    os.write(b);
                }
                os.flush(); //关闭流
                in.close();
                os.close();

                //调用第三方压缩图片
                String newPath = compressionImage(filePath, fileName);
                //上传第三方保存图片
            } catch (Exception e) {
                e.printStackTrace();
                throw new NewException(ErrorCodeEnum.FILE_UPLOAD_FAIL.getCode(), ErrorCodeEnum.FILE_UPLOAD_FAIL.getMsg());
            }
        }
        return true;
    }

    public String compressionImage(String filePath, String fileName) {
        try {
            // 创建OkHttpClient
            OkHttpClient client = new OkHttpClient();

            // 创建表单数据
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);

            // 添加其他参数
            builder.addFormDataPart("name", fileName);
            builder.addFormDataPart("type", "");

            try {
                Path path = Paths.get(filePath);
                String type = Files.probeContentType(path);
                switch (type) {
                    case "image/gif":
                        builder.addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("image/gif"), new File(filePath)));
                        break;
                    case "image/jpeg":
                        builder.addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("image/jpeg"), new File(filePath)));
                        break;
                    case "image/png":
                        builder.addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("image/png"), new File(filePath)));
                        break;
                    default:
                        throw new NewException(ErrorCodeEnum.UN_SUPPORT_IMAGE_TYPE.getCode(), ErrorCodeEnum.UN_SUPPORT_IMAGE_TYPE.getMsg());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 构建请求体
            MultipartBody requestBody = builder.build();

            // 创建请求
            Request request = new Request.Builder()
                    .url(CommonEnum.IMAGE_HANDLE_URL.getValue())
                    .post(requestBody)
                    .build();

            // 发送请求并处理响应
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                ImageInfoResponse info = ToolsUtil.convertToObject(response.body().string(), ImageInfoResponse.class);
                return info.getPath();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new NewException(ErrorCodeEnum.UN_SUPPORT_IMAGE_TYPE.getCode(), ErrorCodeEnum.UN_SUPPORT_IMAGE_TYPE.getMsg());
        }
    }
}
