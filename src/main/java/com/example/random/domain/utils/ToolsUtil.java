package com.example.random.domain.utils;

import com.example.random.domain.common.exception.NewException;
import com.example.random.domain.common.support.ErrorCodeEnum;
import com.example.random.domain.value.UploadInfo;
import com.example.random.interfaces.redis.message.UploadMessage;
import com.example.random.interfaces.redis.producer.RedisQueue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public static Date StringToDate(String date) {
        if (date.length() == 10) {
            date += " 00:00:00";
        }
        Date newDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // 这是转换的格式，根据你实际的字符串格式进行调整
        try {
            newDate = sdf.parse(date);  // 将字符串转换为Date对象
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String DateToString(Date date, String type) {
        if (Objects.isNull(date)) {
            return "";
        }
        String model = "yyyy-MM-dd";
        if (Objects.equals(type, "seconds")) {
            model = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(model);
        return formatter.format(date);
    }

    /**
     * 文件上传
     * @param redisQueue RedisQueue
     * @param upload UploadInfo
     */
    public static void UploadFile(RedisQueue redisQueue, UploadInfo upload, String redisKey) {
        redisQueue.delete(String.format("%s-uploadFile", upload.getUid()));
        int num = 1;
        int total = upload.getFiles().length;
        for (MultipartFile file : upload.getFiles()) {
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
                info.setConfigId(upload.getConfigId());
                info.setAction(upload.getAction());
                if (Strings.isNotEmpty(upload.getTitle())) {
                    info.setName(upload.getTitle());
                }
                if (Strings.isNotEmpty(upload.getDesc())) {
                    info.setDesc(upload.getDesc());
                }
                if (Strings.isNotEmpty(upload.getTitle())) {
                    info.setDate(upload.getDate());
                }
                // 向消息队列写入消息
                String jsonString = ToolsUtil.convertToJson(info);
                redisQueue.push(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
                throw new NewException(ErrorCodeEnum.FILE_UPLOAD_FAIL.getCode(), ErrorCodeEnum.FILE_UPLOAD_FAIL.getMsg());
            }
            double a = (double) num / total;
            String percentage = String.format("%.2f", a);
            redisQueue.setValue(redisKey, percentage, 300);
            num++;
        }
    }
}
