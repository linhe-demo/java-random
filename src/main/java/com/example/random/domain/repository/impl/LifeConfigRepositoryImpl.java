package com.example.random.domain.repository.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.random.domain.common.support.StatusEnum;
import com.example.random.domain.entity.FellingData;
import com.example.random.domain.entity.FoodData;
import com.example.random.domain.entity.FoodReason;
import com.example.random.domain.entity.LifeConfig;
import com.example.random.domain.repository.LifeConfigRepository;
import com.example.random.interfaces.controller.put.request.life.FellingRequest;
import com.example.random.interfaces.controller.put.response.config.ConfigResponse;
import com.example.random.interfaces.mapper.FellingDataMapper;
import com.example.random.interfaces.mapper.FoodDataMapper;
import com.example.random.interfaces.mapper.FoodReasonMapper;
import com.example.random.interfaces.mapper.LifeConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class LifeConfigRepositoryImpl implements LifeConfigRepository {
    private final LifeConfigMapper lifeConfigMapper;
    private final FellingDataMapper fellingDataMapper;
    private final FoodReasonMapper foodReasonMapper;
    private final FoodDataMapper foodDataMapper;
    @Override
    public List<LifeConfig> getLifeConfigData(Integer id) {
        return lifeConfigMapper.selectList(Wrappers.<LifeConfig>lambdaQuery()
                .eq(LifeConfig::getStatus, 2)
                .eq(LifeConfig::getConfigId, id));
    }

    @Override
    public List<LifeConfig> getLifeConfigData(String id) {
        return lifeConfigMapper.selectList(Wrappers.<LifeConfig>lambdaQuery()
                .eq(LifeConfig::getStatus, 2)
                .eq(LifeConfig::getConfigId, id));
    }

    @Override
    public List<LifeConfig> getConfigData() {
        return lifeConfigMapper.selectList(Wrappers.<LifeConfig>lambdaQuery());
    }

    @Override
    public void SaveInfo(String path, String id) {
        LifeConfig info = new LifeConfig();
        info.setConfigId(id);
        info.setStatus(2);
        info.setCreateTime(new Date());
        info.setImgUrl(path);
        lifeConfigMapper.insert(info);
    }

    @Override
    public LifeConfig getConfigById(Integer id) {
        return lifeConfigMapper.selectOne(Wrappers.<LifeConfig>lambdaQuery()
                .eq(LifeConfig::getId, id));
    }

    @Override
    public void removePictureById(Integer id) {
        lifeConfigMapper.deleteById(id);
    }

    @Override
    public List<FellingData> getUserFeeling(Long personAlbumId) {
        return fellingDataMapper.selectList(Wrappers.<FellingData>lambdaQuery()
                .eq(FellingData::getPersonId, personAlbumId)
                .eq(FellingData::getStatus, StatusEnum.STATUS_ON.getCode())
                .orderByAsc(FellingData::getCreateTime));
    }

    @Override
    public void saveFelling(FellingRequest request, Long id) {
        FellingData data = new FellingData();
        data.setTitle(request.getTitle());
        data.setPersonId(id);
        data.setText(request.getFellingText());
        data.setCreateTime(new Date());
        data.setStatus(StatusEnum.STATUS_ON.getCode());
        fellingDataMapper.insert(data);
    }

    @Override
    public List<FoodReason> getFoodReason() {
        return foodReasonMapper.selectList(Wrappers.<FoodReason>lambdaQuery());
    }

    @Override
    public List<FoodData> getFood() {
        return foodDataMapper.selectList(Wrappers.<FoodData>lambdaQuery());
    }
}
