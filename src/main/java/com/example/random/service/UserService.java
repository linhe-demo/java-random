package com.example.random.service;

import com.example.random.domain.common.support.StatusEnum;
import com.example.random.domain.entity.ExtensionData;
import com.example.random.domain.entity.UserBaby;
import com.example.random.domain.entity.UserInfo;
import com.example.random.domain.repository.UserInfoRepository;
import com.example.random.domain.utils.TokenUtil;
import com.example.random.domain.utils.calendar.CalendarUtil;
import com.example.random.domain.value.PregnantDate;
import com.example.random.interfaces.controller.put.response.user.UserFeatureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Lazy
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserInfoRepository userInfoRepository;

    public UserFeatureResponse getFeatureInfo(HttpServletRequest ip) {
        UserFeatureResponse backInfo = new UserFeatureResponse();
        UserInfo user = TokenUtil.getCurrentUser();
        // 检查用户是否孕育
        assert user != null;
        UserBaby baby = userInfoRepository.getBabyConfigById(user.getPersonAlbumId());
        if (!ObjectUtils.isEmpty(baby)) {
            backInfo.setWelcomeStr(String.format("欢迎 %s 来看宝宝 ^_^", Objects.equals(user.getSexy(), StatusEnum.MALE.getCode()) ? StatusEnum.MALE.getMsg() : StatusEnum.FEMALE.getMsg()));
            backInfo.setDueDate(String.format("预产期：%s（离预产期 %s 天）", calculateExpectedDeliveryDate(baby.getPregnancyDate()), CalendarUtil.Countdown(calculateExpectedDeliveryDate(baby.getPregnancyDate()))));
            PregnantDate info = PregnancyCalculator( baby.getPregnancyDate());
            backInfo.setPregnantWeeks(String.format("孕期：孕%s周%s天", info.getWeek(), info.getDay()));
            backInfo.setPregnantWeeksDetail(String.format("孕周详情：%s", info.getDetail()));
            backInfo.setBtn3("宝宝");
            backInfo.setWeek(info.getWeek());
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

    public PregnantDate PregnancyCalculator(Date lastMenstrualPeriod){
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
        return backInfo;
    }
}
