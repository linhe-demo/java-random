package com.example.random.service;

import com.example.random.domain.constant.CommonEnum;
import com.example.random.domain.entity.LifeConfig;
import com.example.random.domain.repository.LifeConfigRepository;
import com.example.random.domain.utils.BeanCopierUtil;
import com.example.random.interfaces.client.LogClient;
import com.example.random.interfaces.client.vo.request.LogInfoRequest;
import com.example.random.interfaces.controller.put.request.life.LifeRequest;
import com.example.random.interfaces.controller.put.response.config.ConfigResponse;
import com.example.random.interfaces.controller.put.response.life.LifeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LifeMomentService {
    private final LifeConfigRepository lifeConfigRepository;
    private final LogClient logClient;

    public List<LifeResponse> momentData(LifeRequest request) {
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
        LogInfoRequest param = new LogInfoRequest();
        param.setAction("photo");
        param.setActionUser("java-spring-boot");
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
            if (i.getUpdateTime() != null ){
                tmpData.setUpdateTime(dateFormat.format(i.getUpdateTime()));
            }
            backData.add(tmpData);
        });
        return backData;
    }
}
