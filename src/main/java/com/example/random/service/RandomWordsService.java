package com.example.random.service;

import com.example.random.domain.entity.LifeConfig;
import com.example.random.domain.repository.LifeConfigRepository;
import com.example.random.interfaces.controller.put.request.life.LifeRequest;
import com.example.random.interfaces.controller.put.response.life.LifeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RandomWordsService {
    private final LifeConfigRepository lifeConfigRepository;

    public List<LifeResponse> getRandomWords(LifeRequest request) {
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
                info.setImgUrl(i.getImgUrl());
                info.setText(i.getText());
                list.add(info);
            });
        }
        return list;
    }
}