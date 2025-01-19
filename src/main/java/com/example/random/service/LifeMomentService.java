package com.example.random.service;

import com.example.random.domain.common.exception.NewException;
import com.example.random.domain.common.support.ErrorCodeEnum;
import com.example.random.domain.common.support.StatusEnum;
import com.example.random.domain.constant.CommonEnum;
import com.example.random.domain.entity.*;
import com.example.random.domain.repository.AlbumConfigRepository;
import com.example.random.domain.repository.DateListRepository;
import com.example.random.domain.repository.LifeConfigRepository;
import com.example.random.domain.repository.UserInfoRepository;
import com.example.random.domain.utils.*;
import com.example.random.domain.utils.calendar.CalendarUtil;
import com.example.random.domain.value.CalendarInfo;
import com.example.random.domain.value.RedisInfo;
import com.example.random.domain.value.UploadInfo;
import com.example.random.interfaces.client.LogClient;
import com.example.random.interfaces.client.vo.request.LogInfoRequest;
import com.example.random.interfaces.client.vo.response.ImageInfoResponse;
import com.example.random.interfaces.controller.put.request.album.AlbumConfigAddRequest;
import com.example.random.interfaces.controller.put.request.life.FellingRequest;
import com.example.random.interfaces.controller.put.request.life.LifeRequest;
import com.example.random.interfaces.controller.put.request.life.RemovePictureRequest;
import com.example.random.interfaces.controller.put.request.user.AlbumListRequest;
import com.example.random.interfaces.controller.put.request.user.DateListRequest;
import com.example.random.interfaces.controller.put.request.user.RegisterRequest;
import com.example.random.interfaces.controller.put.request.user.UserRequest;
import com.example.random.interfaces.controller.put.response.config.ConfigResponse;
import com.example.random.interfaces.controller.put.response.life.GoodsResponse;
import com.example.random.interfaces.controller.put.response.life.LifeFellingResponse;
import com.example.random.interfaces.controller.put.response.life.LifeResponse;
import com.example.random.interfaces.controller.put.response.user.AlbumResponse;
import com.example.random.interfaces.controller.put.response.user.CalendarResponse;
import com.example.random.interfaces.controller.put.response.user.RegisterResponse;
import com.example.random.interfaces.controller.put.response.user.UserResponse;
import com.example.random.interfaces.mq.message.UploadImgMessage;
import com.example.random.interfaces.redis.message.UploadMessage;
import com.example.random.interfaces.redis.producer.RedisQueue;
import lombok.RequiredArgsConstructor;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


@Lazy
@Service
@RequiredArgsConstructor
public class LifeMomentService {
    private final LifeConfigRepository lifeConfigRepository;
    private final UserInfoRepository userInfoRepository;
    private final AlbumConfigRepository albumConfigRepository;
    private final LogClient logClient;
    private final DateListRepository dateListRepository;

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
                info.setImgUrl(CommonEnum.Yun.IMAGE_FILE_PATH.getData() + i.getImgUrl());
                info.setText(i.getText());
                info.setId(i.getId());
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
            tmpData.setImgUrl(CommonEnum.Yun.IMAGE_FILE_PATH.getData() + i.getImgUrl());
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
            redisQueue.setValue(String.format("%s-linHeDemo", userInfo.getId()), ToolsUtil.convertToJson(redisInfo), 86400 * 365);
            backInfo.setExpiredTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + 86400 * 365);
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
        Long id = userInfoRepository.saveUserInfo(request);
        int year = LocalDate.now().getYear();
        //初始化用户相册数据
        dateListRepository.save(id, String.valueOf(year));
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
        String bucket = redisQueue.getValue(String.format("%s-linHeDemo", Objects.requireNonNull(user).getId()));

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
        UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.setUid(user.getId());
        uploadInfo.setFiles(files);
        uploadInfo.setConfigId(String.valueOf(configId));
        uploadInfo.setAction("add-image");
        String redisKey = String.format("%s-uploadFile", user.getId());
        redisQueue.delete(redisKey);

        ToolsUtil.UploadFile(redisQueue, uploadInfo, redisKey);

        LogInfoRequest param = new LogInfoRequest();
        param.setAction("upload-image");
        param.setActionUser(Objects.requireNonNull(user).getUserName());
        param.setIp(ToolsUtil.getIp(ip));
        //记录日志
        logClient.saveLogInfo(param);
        redisQueue.delete(String.format("%s-uploadFile", user.getId()));
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
                    .url(CommonEnum.Yun.IMAGE_HANDLE_URL.getData())
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

    public Integer progress() {
        UserInfo user = TokenUtil.getCurrentUser();
        assert user != null;
        String value = redisQueue.getValue(String.format("%s-uploadFile", user.getId()));
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

    public Boolean removePicture(RemovePictureRequest request, HttpServletRequest ip) {
        UserInfo user = TokenUtil.getCurrentUser();
        assert user != null;
        //检查当前图片是否属于该账户
        LifeConfig info = lifeConfigRepository.getConfigById(request.getId());
        if (Objects.isNull(info)) {
            throw new NewException(ErrorCodeEnum.CONFIG_NOT_FOUND.getCode(), ErrorCodeEnum.CONFIG_NOT_FOUND.getMsg());
        }
        String configId = info.getConfigId();
        AlbumConfig config = albumConfigRepository.getAlbumConfigById(configId);
        if (Objects.isNull(config)) {
            throw new NewException(ErrorCodeEnum.CONFIG_NOT_FOUND.getCode(), ErrorCodeEnum.CONFIG_NOT_FOUND.getMsg());
        }
        if (!Objects.equals(user.getPersonAlbumId(), config.getPersonAlbumId())) {
            throw new NewException(ErrorCodeEnum.USER_NOT_PERMISSION.getCode(), ErrorCodeEnum.USER_NOT_PERMISSION.getMsg());
        }
        UploadMessage saveInfo = new UploadMessage();
        saveInfo.setId(Long.valueOf(request.getId()));
        saveInfo.setAction("remove-image");
        saveInfo.setPath(info.getImgUrl());
        // 向消息队列写入消息
        String jsonString = ToolsUtil.convertToJson(saveInfo);
        redisQueue.push(jsonString);

        // 将表中的数据移除
        lifeConfigRepository.removePictureById(request.getId());

        //记录日志
        LogInfoRequest param = new LogInfoRequest();
        param.setAction(String.format("remove-image id: %s", request.getId()));
        param.setActionUser(Objects.requireNonNull(user).getUserName());
        param.setIp(ToolsUtil.getIp(ip));
        logClient.saveLogInfo(param);

        return true;
    }

    public CalendarResponse getDate() {
        UserInfo user = TokenUtil.getCurrentUser();
        CalendarInfo dateInfo = CalendarUtil.getCurrentDate();
        CalendarResponse backInfo = new CalendarResponse();
        BeanCopierUtil.copy(dateInfo, backInfo);
        // 获取用户配置信息
        UserConfig userConfig = userInfoRepository.getUserConfigByAlbumId(user.getPersonAlbumId());
        if (!ObjectUtils.isEmpty(userConfig)) {
            backInfo.setMarryDay(CalendarUtil.getTimeApart("结婚：", ToolsUtil.DateToString(userConfig.getMarryDate(), "day")));
            backInfo.setFirstMeeting(CalendarUtil.getTimeApart("初见：", ToolsUtil.DateToString(userConfig.getFirstMeetDate(), "day")));
            backInfo.setCertificateDay(CalendarUtil.getTimeApart("领证：", ToolsUtil.DateToString(userConfig.getCertificateDate(), "day")));
            backInfo.setFirework(Objects.equals(userConfig.getFirework(), StatusEnum.FIREWORK_ON.getCode()));
            Date current = new Date();
            String currentDate = ToolsUtil.DateToString(current, "anniversary");

            if (user.getId() == 2) {
                backInfo.setAnniversary("不能吃油炸食物");
            }
            if (Objects.equals(currentDate, ToolsUtil.DateToString(userConfig.getMarryDate(), "anniversary"))) {
                backInfo.setFirework(true);
                backInfo.setAnniversary("结婚周年纪念日");
            }
            if (Objects.equals(currentDate, ToolsUtil.DateToString(userConfig.getFirstMeetDate(), "anniversary"))) {
                backInfo.setFirework(true);
                backInfo.setAnniversary("初见周年纪念日");
            }
            if (Objects.equals(currentDate, ToolsUtil.DateToString(userConfig.getCertificateDate(), "anniversary"))) {
                backInfo.setFirework(true);
                backInfo.setAnniversary("领证周年纪念日");
            }
        }
        return backInfo;
    }

    public List<LifeFellingResponse> felling() {
        UserInfo user = TokenUtil.getCurrentUser();
        assert user != null;
        List<FellingData> feelings = lifeConfigRepository.getUserFeeling(user.getPersonAlbumId());
        if (CollectionUtils.isEmpty(feelings)) {
            return new ArrayList<>();
        }
        List<LifeFellingResponse> backInfo = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/d");
        feelings.forEach(i -> {
            LifeFellingResponse tmp = new LifeFellingResponse();
            tmp.setTitle(i.getTitle());
            tmp.setText(i.getText());
            tmp.setDate(dateFormat.format(i.getCreateTime()));
            backInfo.add(tmp);
        });
        return backInfo;
    }

    public Boolean addFelling(FellingRequest request, HttpServletRequest ip) {
        UserInfo user = TokenUtil.getCurrentUser();
        assert user != null;
        if (request.getTitle().isEmpty()) {
            throw new NewException(ErrorCodeEnum.TITLE_NOT_EMPTY.getCode(), ErrorCodeEnum.TITLE_NOT_EMPTY.getMsg());
        }
        if (request.getFellingText().isEmpty()) {
            throw new NewException(ErrorCodeEnum.CONTENT_NOT_EMPTY.getCode(), ErrorCodeEnum.CONTENT_NOT_EMPTY.getMsg());
        }
        lifeConfigRepository.saveFelling(request, user.getPersonAlbumId());
        LogInfoRequest param = new LogInfoRequest();
        param.setAction("add-felling");
        param.setActionUser(Objects.requireNonNull(user).getUserName());
        param.setIp(ToolsUtil.getIp(ip));
        //记录日志
        logClient.saveLogInfo(param);
        return true;
    }

    public List<GoodsResponse> getFood() {
        List<GoodsResponse> backInfo = new ArrayList<>();
        UserInfo user = TokenUtil.getCurrentUser();
        assert user != null;
        Map<Integer, String> goodsReasonMap = lifeConfigRepository.getFoodReason().stream().collect(Collectors.toMap(FoodReason::getTypeId,
                FoodReason::getReason,
                (existingValue, newValue) -> existingValue));
        List<FoodData> foods = lifeConfigRepository.getFood();
        for (FoodData item: foods) {
            GoodsResponse tmp = new GoodsResponse();
            tmp.setName(item.getFoodName());
            tmp.setLevel(item.getStatus());
            tmp.setReason(goodsReasonMap.get(item.getType()));
            backInfo.add(tmp);
        }
        return backInfo;
    }
}
