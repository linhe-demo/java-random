package com.example.random.domain.repository;

import com.example.random.domain.entity.LifeConfig;

import java.util.List;

public interface LifeConfigRepository {
    /**
     * 获取生活照片配置
     *
     * @param num 照片数量
     */
    List<LifeConfig> getLifeConfigData(Integer id);

    /**
     * 获取配置数据
     *
     * @return List<ConfigResponse>
     */
    List<LifeConfig> getConfigData();
}
