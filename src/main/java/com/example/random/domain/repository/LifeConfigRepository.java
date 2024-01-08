package com.example.random.domain.repository;

import com.example.random.domain.entity.FellingData;
import com.example.random.domain.entity.LifeConfig;

import java.util.List;

public interface LifeConfigRepository {
    /**
     * 获取生活照片配置
     *
     * @param id 相册id
     */
    List<LifeConfig> getLifeConfigData(Integer id);

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
    void SaveInfo(String path, Integer id);

    LifeConfig getConfigById(Integer id);

    void removePictureById(Integer id);


    List<FellingData> getUserFeeling(Long personAlbumId);
}
