package com.example.random.service;

import com.example.random.domain.common.exception.NewException;
import com.example.random.domain.common.support.ErrorCodeEnum;
import com.example.random.domain.constant.CommonEnum;
import com.example.random.domain.entity.AlbumConfig;
import com.example.random.domain.entity.DateConfig;
import com.example.random.domain.entity.LifeConfig;
import com.example.random.domain.entity.UserInfo;
import com.example.random.domain.repository.AlbumConfigRepository;
import com.example.random.domain.repository.DateListRepository;
import com.example.random.domain.repository.LifeConfigRepository;
import com.example.random.domain.repository.UserInfoRepository;
import com.example.random.domain.utils.*;
import com.example.random.domain.value.RedisInfo;
import com.example.random.interfaces.client.LogClient;
import com.example.random.interfaces.client.vo.request.LogInfoRequest;
import com.example.random.interfaces.client.vo.response.ImageInfoResponse;
import com.example.random.interfaces.controller.put.request.album.AlbumConfigAddRequest;
import com.example.random.interfaces.controller.put.request.life.LifeRequest;
import com.example.random.interfaces.controller.put.request.user.AlbumListRequest;
import com.example.random.interfaces.controller.put.request.user.DateListRequest;
import com.example.random.interfaces.controller.put.request.user.RegisterRequest;
import com.example.random.interfaces.controller.put.request.user.UserRequest;
import com.example.random.interfaces.controller.put.response.config.ConfigResponse;
import com.example.random.interfaces.controller.put.response.life.LifeResponse;
import com.example.random.interfaces.controller.put.response.user.AlbumResponse;
import com.example.random.interfaces.controller.put.response.user.RegisterResponse;
import com.example.random.interfaces.controller.put.response.user.UserResponse;
import com.example.random.interfaces.mq.message.UploadImgMessage;
import com.example.random.interfaces.redis.message.UploadMessage;
import com.example.random.interfaces.redis.producer.RedisQueue;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


@Lazy
@Service
@RequiredArgsConstructor
public class LifeMomentService {
    private final LifeConfigRepository lifeConfigRepository;
    private final UserInfoRepository userInfoRepository;
    private final AlbumConfigRepository albumConfigRepository;
    private final LogClient logClient;
    private final DateListRepository dateListRepository;
    private final RedissonClient redissonClient;

    @Autowired
    private RedisQueue redisQueue;

    public List<LifeResponse> momentData(LifeRequest request, HttpServletRequest ip) {
        UserInfo user = TokenUtil.getCurrentUser();
        List<LifeResponse> list = new ArrayList<>();
        List<LifeConfig> data;
        //检查用户是否拥有该相册权限
        assert user != null;
        AtomicBoolean status = new AtomicBoolean(false);
        List<AlbumConfig> albumData = albumConfigRepository.getAlbumConfig(user.getPersonAlbumId(), null, null);
        albumData.forEach(i -> {
            if (Objects.equals(i.getId(), request.getId())) {
                status.set(true);
            }
        });
        if (!status.get()) {
            throw new NewException(ErrorCodeEnum.WITHOUT_PERMISSION.getCode(), ErrorCodeEnum.WITHOUT_PERMISSION.getMsg());
        }
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
                info.setImgUrl(CommonEnum.IMAGE_FILE_PATH.getValue() + i.getImgUrl());
                info.setText(i.getText());
                list.add(info);
            });
        }
        LogInfoRequest param = new LogInfoRequest();
        param.setAction("photo");
        param.setActionUser(Objects.requireNonNull(user).getUserName());
        param.setIp(ToolsUtil.getIp(ip));
        //记录日志
        logClient.saveLogInfo(param);
        return list;
    }

    public List<ConfigResponse> configData() {
        UserInfo user = TokenUtil.getCurrentUser();
        List<ConfigResponse> backData = new ArrayList<>();
        assert user != null;
        List<LifeConfig> data = lifeConfigRepository.getConfigData();
        data.forEach(i -> {
            ConfigResponse tmpData = new ConfigResponse();
            BeanCopierUtil.copy(i, tmpData);
            tmpData.setImgUrl(CommonEnum.IMAGE_FILE_PATH.getValue() + i.getImgUrl());
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

    public List<AlbumResponse> getAlbumList(AlbumListRequest request, HttpServletRequest ip) {
        UserInfo user = TokenUtil.getCurrentUser();
        List<AlbumResponse> backInfo = new ArrayList<>();
        assert user != null;

        List<AlbumConfig> list = albumConfigRepository.getAlbumConfig(user.getPersonAlbumId(), ToolsUtil.StringToDate(String.format("%s-01-01 00:00:00", request.getDate())), ToolsUtil.StringToDate(String.format("%s-12-31 23:59:59", request.getDate())));
        final Integer[] num = {1};
        list.forEach(i -> {
            AlbumResponse albumResponse = new AlbumResponse();
            BeanCopierUtil.copy(i, albumResponse);
            albumResponse.setDate(i.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            albumResponse.setTheme(String.format("right theme-%d", num[0] % 4));
            backInfo.add(albumResponse);
            num[0]++;
        });
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

    public List<String> dateList(DateListRequest request, HttpServletRequest ip) {
        UserInfo user = TokenUtil.getCurrentUser();
        assert user != null;
        List<DateConfig> list = dateListRepository.getUserDateConfig(user.getPersonAlbumId());
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<String> backInfo = new ArrayList<>();
        list.forEach(i -> {
            backInfo.add(i.getDate());
        });
        return backInfo;
    }

    public boolean test() {
        for (int i = 0; i < 1000; i++) {
            UploadImgMessage info = new UploadImgMessage();
            info.setPath("hello world！");
            info.setId(i);
//            uploadImgProducer.SendMessage(info.toString());

            redisQueue.push(info.toString());
        }
        return true;
    }

    public Boolean upload(MultipartFile[] files, Integer configId, HttpServletRequest ip) {
        UserInfo user = TokenUtil.getCurrentUser();
        assert user != null;
        if (files == null || ObjectUtils.isEmpty(files)) {
            throw new NewException(ErrorCodeEnum.FILE_IS_EMPTY.getCode(), ErrorCodeEnum.FILE_IS_EMPTY.getMsg());
        }
        redisQueue.delete(String.format("%s-uploadFile", user.getId()));
        int num = 1;
        int total = files.length;
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            assert fileName != null;
            List<String> tmpList = Arrays.asList(fileName.split("\\."));
            String tmpFormat = tmpList.get(tmpList.size() - 1);
            try {
                //保存文件到本地
                File mkdir = new File("/home/static/upload");
                if (!mkdir.exists()) {
                    boolean res = mkdir.mkdirs();
                    if (res) {
                        throw new NewException(ErrorCodeEnum.FAIL_CREATE_FILE.getCode(), ErrorCodeEnum.FAIL_CREATE_FILE.getMsg());
                    }
                }
                long id = System.currentTimeMillis();
                String filePath = String.format("%s/%s.%s", "/home/static/upload", id, tmpFormat);
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
                UploadMessage info = new UploadMessage();
                info.setId(id);
                info.setPath(filePath);
                info.setConfigId(configId);
                // 向消息队列写入消息
                String jsonString = ToolsUtil.convertToJson(info);
                redisQueue.push(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
                throw new NewException(ErrorCodeEnum.FILE_UPLOAD_FAIL.getCode(), ErrorCodeEnum.FILE_UPLOAD_FAIL.getMsg());
            }
            double a = (double) num / total;
            String percentage = String.format("%.2f", a);
            redisQueue.setValue(String.format("%s-uploadFile", user.getId()), percentage, 0);
            num++;
        }
        LogInfoRequest param = new LogInfoRequest();
        param.setAction("upload-image");
        param.setActionUser(Objects.requireNonNull(user).getUserName());
        param.setIp(ToolsUtil.getIp(ip));
        //记录日志
        logClient.saveLogInfo(param);
        return true;
    }

    public Boolean albumAdd(AlbumConfigAddRequest request, HttpServletRequest ip) {
        UserInfo user = TokenUtil.getCurrentUser();
        assert user != null;
        //记录相册年份信息
        String year = String.valueOf(request.getDate().getYear());
        DateConfig info = dateListRepository.getDateConfigByIdAndYear(user.getPersonAlbumId(), year);
        if (ObjectUtils.isEmpty(info)) {
            dateListRepository.save(user.getPersonAlbumId(), year);
        }
        int res = albumConfigRepository.saveAlbumConfig(request.getName(), request.getDesc(), request.getDate(), user.getPersonAlbumId());
        if (res > 0) {
            LogInfoRequest param = new LogInfoRequest();
            param.setAction("add-album");
            param.setActionUser(Objects.requireNonNull(user).getUserName());
            param.setIp(ToolsUtil.getIp(ip));
            //记录日志
            logClient.saveLogInfo(param);
            return true;
        } else {
            throw new NewException(ErrorCodeEnum.FILE_UPLOAD_FAIL.getCode(), ErrorCodeEnum.FILE_UPLOAD_FAIL.getMsg());
        }
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

    public Integer progress()  {
        UserInfo user = TokenUtil.getCurrentUser();
        assert user != null;
        String value =  redisQueue.getValue(String.format("%s-uploadFile", user.getId()));
        if (Objects.isNull(value)) {
            return 0;
        }
        double percentage = Double.parseDouble(value) * 100;
        if (percentage == 100) {
            redisQueue.delete(String.format("%s-uploadFile", user.getId()));
        }
        return (int) percentage;
    }

    public Boolean cleanRedis() {
        UserInfo user = TokenUtil.getCurrentUser();
        assert user != null;
        redisQueue.delete(String.format("%s-uploadFile", user.getId()));
        return true;
    }
}
