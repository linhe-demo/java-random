package com.example.random.domain.repository;

import com.example.random.domain.entity.FellingData;
import com.example.random.domain.entity.FoodData;
import com.example.random.domain.entity.FoodReason;
import com.example.random.domain.entity.LifeConfig;
import com.example.random.interfaces.controller.put.request.life.FellingRequest;

import java.util.List;
import java.util.Map;

public interface LifeConfigRepository {
    /**
     * 获取生活照片配置
     *
     * @param id 相册id
     */
    List<LifeConfig> getLifeConfigData(Integer id);

    List<LifeConfig> getLifeConfigData(String id);

    /**
     * 获取配置数据
     *
     * @return List<ConfigResponse>
     */
    List<LifeConfig> getConfigData();

    /**
     * 保存照片信息
     *
     * @param path 照片路径
     * @param id   相册id
     */
    void SaveInfo(String path, String id);

    LifeConfig getConfigById(Integer id);

    void removePictureById(Integer id);


    List<FellingData> getUserFeeling(Long personAlbumId);

    void saveFelling(FellingRequest request, Long id);

    List<FoodReason> getFoodReason();

    List<FoodData> getFood();
}
