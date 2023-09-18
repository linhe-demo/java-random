package com.example.random.domain.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ToolsUtil {
    /**
     * 将类转换为 json 字符串
     *
     * @return 转换后的json
     */
    public static String convertToJson(Object clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将json 字符串转换为 对象类
     *
     * @param param json 字符串
     * @param <T>   泛型 目标类
     * @return 实例化后的目标类
     */
    public static <T> T convertToObject(String param, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(param, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对 list 进行等分
     *
     * @param list   需要等分的列表
     * @param subNum 等分后每个list大小
     * @param <T>    泛型
     * @return List<List < T>>
     */
    public static <T> List<List<T>> splistList(List<T> list, int subNum) {
        List<List<T>> tNewList = new ArrayList<List<T>>();
        int priIndex = 0;
        int lastPriIndex = 0;
        int insertTimes = list.size() / subNum;
        List<T> subList = new ArrayList<>();
        for (int i = 0; i <= insertTimes; i++) {
            priIndex = subNum * i;
            lastPriIndex = priIndex + subNum;
            if (i == insertTimes) {
                subList = list.subList(priIndex, list.size());
            } else {
                subList = list.subList(priIndex, lastPriIndex);
            }
            if (subList.size() > 0) {
                tNewList.add(subList);
            }
        }
        return tNewList;
    }

    public static String getIp(HttpServletRequest ip) {
        String clientIp = ip.getHeader("X-Forwarded-For");
        if (clientIp == null) {
            clientIp = ip.getRemoteAddr();
        }
        return clientIp;
    }

    public static String convertTimestampToStandardFormatDay(Long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }
}
