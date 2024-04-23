package com.example.random.service;

import com.example.random.domain.common.exception.NewException;
import com.example.random.domain.common.support.ErrorCodeEnum;
import com.example.random.domain.common.support.StatusEnum;
import com.example.random.domain.constant.CommonEnum;
import com.example.random.domain.entity.*;
import com.example.random.domain.repository.AlbumConfigRepository;
import com.example.random.domain.repository.LifeConfigRepository;
import com.example.random.domain.repository.UserInfoRepository;
import com.example.random.domain.utils.TokenUtil;
import com.example.random.domain.utils.ToolsUtil;
import com.example.random.domain.utils.calendar.CalendarUtil;
import com.example.random.domain.value.BabyInfo;
import com.example.random.domain.value.BabyLifeImg;
import com.example.random.domain.value.PregnantDate;
import com.example.random.domain.value.UploadInfo;
import com.example.random.interfaces.client.LogClient;
import com.example.random.interfaces.client.vo.request.LogInfoRequest;
import com.example.random.interfaces.controller.put.response.user.BabyHighlightResponse;
import com.example.random.interfaces.controller.put.response.user.BabyLifeResponse;
import com.example.random.interfaces.controller.put.response.user.UserFeatureResponse;
import com.example.random.interfaces.controller.put.response.user.UserImageResponse;
import com.example.random.interfaces.redis.producer.RedisQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Lazy
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserInfoRepository userInfoRepository;
    private final LogClient logClient;
    private final LifeConfigRepository lifeConfigRepository;
    private final AlbumConfigRepository albumConfigRepository;

    @Autowired
    private RedisQueue redisQueue;

    public UserFeatureResponse getFeatureInfo(HttpServletRequest ip) {
        Map<Integer, String> sexyMap = initSexy();
        UserFeatureResponse backInfo = new UserFeatureResponse();
        UserInfo user = TokenUtil.getCurrentUser();
        // 检查用户是否孕育
        assert user != null;
        List<UserBaby> baby = userInfoRepository.getBabyConfigById(user.getPersonAlbumId());
        if (!ObjectUtils.isEmpty(baby)) {
            backInfo.setWelcomeStr(String.format("欢迎 %s 来看宝宝 ^_^", Objects.equals(user.getSexy(), StatusEnum.MALE.getCode()) ? StatusEnum.MALE.getMsg() : StatusEnum.FEMALE.getMsg()));
            List<BabyInfo> babyInfos = new ArrayList<>();
            for (UserBaby item : baby) {
                if (Objects.equals(item.getStatus(), StatusEnum.BABY_OFF.getCode())) {
                    long countDown = CalendarUtil.Countdown(calculateExpectedDeliveryDate(item.getPregnancyDate()));
                    backInfo.setDueDate(String.format("预产期：%s（离预产期 %s 天）", calculateExpectedDeliveryDate(item.getPregnancyDate()), countDown));
                    BigDecimal bd = BigDecimal.valueOf((280.00 - countDown) / 280).setScale(2, RoundingMode.DOWN);
                    double newNumber = bd.doubleValue();
                    backInfo.setPercentage((int) (newNumber * 100));
                }
                if (!Objects.isNull(item.getPregnancyDate())) {
                    PregnantDate info = PregnancyCalculator(item.getPregnancyDate());
                    if (info.getDay() > 0) {
                        backInfo.setPregnantWeeks(String.format("孕期：孕%s周%s天", info.getWeek(), info.getDay()));
                    } else {
                        backInfo.setPregnantWeeks(String.format("孕期：孕 %s 周", info.getWeek()));
                    }
                    backInfo.setPregnantWeeksDetail(String.format("孕周详情：%s", info.getDetail()));
                    backInfo.setWeek(info.getWeek());
                    backInfo.setNoticeValue(info.getExtraValue());
                }
                backInfo.setBtn3("宝宝");
                BabyInfo tmpBaby = new BabyInfo();
                tmpBaby.setNickName(Objects.equals(item.getNickName(), "") ? "未冠名" : item.getNickName());
                tmpBaby.setRealName(Objects.equals(item.getRealName(), "") ? "未冠名" : item.getRealName());
                tmpBaby.setSexy(sexyMap.get(item.getSexy()));
                if (Objects.isNull(item.getLunarBirthday())) {
                    tmpBaby.setBirthday(Objects.equals(ToolsUtil.DateToString(item.getBirthday(), "day"), "") ? "未知" : ToolsUtil.DateToString(item.getBirthday(), "day"));
                } else {
                    tmpBaby.setBirthday(Objects.equals(ToolsUtil.DateToString(item.getBirthday(), "day"), "") ? "未知" : ToolsUtil.DateToString(item.getBirthday(), "day") + "（" + item.getLunarBirthday() + "）");
                }
                tmpBaby.setAge(Objects.equals(calculateAge(item.getBirthday()), 0) ? "未知" : calculateAge(item.getBirthday()).toString());
                babyInfos.add(tmpBaby);
                if (Objects.equals(item.getStatus(), StatusEnum.BABY_OFF.getCode())) {
                    backInfo.setBtnStatus(true);
                }
            }
            backInfo.setBabyInfo(babyInfos);
        } else {
            backInfo.setWelcomeStr(String.format("欢迎 %s 来参观未来 ^_^", user.getNickname()));
            backInfo.setBtn3("未来");
        }
        backInfo.setBtn1("岁月");
        backInfo.setBtn2("星闪");
        return backInfo;
    }

    public static String calculateExpectedDeliveryDate(Date lastMenstrualPeriod) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastMenstrualPeriod);
        // 加上280天（大约40周）
        calendar.add(Calendar.DAY_OF_MONTH, 280);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 返回计算后的日期
        return sdf.format(calendar.getTime());
    }

    public PregnantDate PregnancyCalculator(Date lastMenstrualPeriod) {
        PregnantDate backInfo = new PregnantDate();
        // 将Date转换为Instant
        Instant instant = lastMenstrualPeriod.toInstant();

        // 获取系统默认时区
        ZoneId zoneId = ZoneId.systemDefault();

        // 将Instant转换为ZonedDateTime
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);

        // 从ZonedDateTime提取LocalDate
        LocalDate localDate = zonedDateTime.toLocalDate();

        LocalDate currentDate = LocalDate.now();
        // 计算孕期周数和天数
        int weeks, days;
        long daysSinceLMP = ChronoUnit.DAYS.between(localDate, currentDate);
        weeks = (int) (daysSinceLMP / 7);
        days = (int) (daysSinceLMP % 7);

        ExtensionData info = userInfoRepository.findDetailById(weeks);

        backInfo.setWeek(weeks);
        backInfo.setDay(days);
        backInfo.setDetail(info.getValue());
        backInfo.setExtraValue(info.getExtraValue());
        return backInfo;
    }

    public Map<Integer, String> initSexy() {
        Map<Integer, String> sexyMap = new HashMap<>();
        sexyMap.put(0, "未知");
        sexyMap.put(1, "男");
        sexyMap.put(2, "女");
        return sexyMap;
    }

    public Integer calculateAge(Date birthDate) {
        if (Objects.isNull(birthDate)) {
            return 0;
        }
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        LocalDate birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(birthLocalDate, currentDate);
        int years = period.getYears();

        // 如果当前月份和日期小于出生月份和日期，那么年龄减一
        if (currentDate.getMonthValue() < birthLocalDate.getMonthValue() ||
                (currentDate.getMonthValue() == birthLocalDate.getMonthValue() &&
                        currentDate.getDayOfMonth() < birthLocalDate.getDayOfMonth())) {
            years--;
        }
        return years;
    }

    public Boolean babyUpload(String name, String desc, String date, MultipartFile[] files, HttpServletRequest ip) {
        UserInfo user = TokenUtil.getCurrentUser();
        if (files == null || ObjectUtils.isEmpty(files)) {
            throw new NewException(ErrorCodeEnum.FILE_IS_EMPTY.getCode(), ErrorCodeEnum.FILE_IS_EMPTY.getMsg());
        }
        UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.setUid(user.getId());
        uploadInfo.setFiles(files);

        uploadInfo.setAction("add-baby");
        uploadInfo.setTitle(name);
        uploadInfo.setDesc(desc);
        uploadInfo.setDate(date);
        String redisKey = String.format("%s-baby-file", user.getId());
        redisQueue.delete(redisKey);

        //保存宝宝记录
        Integer id = userInfoRepository.saveBabyLifeInfo(user.getPersonAlbumId(), name, desc, date);
        uploadInfo.setConfigId(String.valueOf(id));

        ToolsUtil.UploadFile(redisQueue, uploadInfo, redisKey);

        LogInfoRequest param = new LogInfoRequest();
        param.setAction("upload-baby-image");
        param.setActionUser(Objects.requireNonNull(user).getUserName());
        param.setIp(ToolsUtil.getIp(ip));
        //记录日志
        logClient.saveLogInfo(param);
        redisQueue.delete(redisKey);
        return true;
    }

    public List<BabyLifeResponse> babyLife() {
        List<BabyLifeResponse> backInfo = new ArrayList<>();
        UserInfo user = TokenUtil.getCurrentUser();
        assert user != null;
        List<BabyLifeData> info = userInfoRepository.getBabyLifeInfo(user.getPersonAlbumId());
        info.forEach(i -> {
            BabyLifeResponse tmp = new BabyLifeResponse();
            tmp.setId(i.getId());
            tmp.setTitle(i.getTitle());
            tmp.setText(i.getText());
            tmp.setDate(ToolsUtil.DateToString(i.getDate(), "day"));
            backInfo.add(tmp);
        });
        return backInfo;
    }

    public BabyHighlightResponse babyHighlight(String id) {
        BabyHighlightResponse backInfo = new BabyHighlightResponse();
        List<LifeConfig> data = lifeConfigRepository.getLifeConfigData("baby-" + id);
        if (CollectionUtils.isEmpty(data)) {
            return backInfo;
        }
        List<String> list = new ArrayList<>();
        List<BabyLifeImg> dataList = new ArrayList<>();
        data.forEach(i -> {
            String url = CommonEnum.IMAGE_FILE_PATH.getValue() + i.getImgUrl();
            BabyLifeImg tmp = new BabyLifeImg();
            tmp.setUrl(url);
            dataList.add(tmp);
            list.add(url);
        });
        backInfo.setList(list);
        backInfo.setData(dataList);
        return backInfo;
    }

    /**
     * 获取用户随机六张岁月照片
     * @return List<UserImageResponse>
     */
    public List<UserImageResponse> imageList() {
        List<UserImageResponse> backInfo = new ArrayList<>();
        UserInfo user = TokenUtil.getCurrentUser();
        List<AlbumConfig> configList = albumConfigRepository.getAlbumConfigByPersonId(user.getPersonAlbumId());
        if (!CollectionUtils.isEmpty(configList)) {
            List<Integer> lifeConfigId = configList.stream().map(AlbumConfig::getId).collect(Collectors.toList());
            List<LifeConfig> lifeConfigs = lifeConfigRepository.getRandomByIds(lifeConfigId);
            Random rand = new Random();
            Map<Integer, Integer> randomMap = new HashMap<>();
            List<LifeConfig> newLifeConfigs = new ArrayList<>();
            while (true) {
                int randomNum = rand.nextInt(lifeConfigs.size());
                if (!randomMap.containsKey(randomNum)) {
                    randomMap.put(randomNum, randomNum);
                    newLifeConfigs.add(lifeConfigs.get(randomNum));
                    if (Objects.equals(newLifeConfigs.size(), StatusEnum.IMAGE_NUM.getCode())) {
                        break;
                    }
                }
            }
            backInfo = newLifeConfigs.stream().map(i -> new UserImageResponse(String.format("%s%s", CommonEnum.IMAGE_FILE_PATH.getValue(), i.getImgUrl()), true)).collect(Collectors.toList());
        }
        return backInfo;
    }
}
