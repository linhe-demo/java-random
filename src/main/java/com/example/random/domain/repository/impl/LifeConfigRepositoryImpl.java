package com.example.random.domain.repository.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.random.domain.entity.LifeConfig;
import com.example.random.domain.repository.LifeConfigRepository;
import com.example.random.interfaces.controller.put.response.config.ConfigResponse;
import com.example.random.interfaces.mapper.LifeConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LifeConfigRepositoryImpl implements LifeConfigRepository {
    private final LifeConfigMapper lifeConfigMapper;

    @Override
    public List<LifeConfig> getLifeConfigData(Integer id) {
        return lifeConfigMapper.selectList(Wrappers.<LifeConfig>lambdaQuery()
                .eq(LifeConfig::getStatus, 2)
                .eq(LifeConfig::getConfigId, id));
    }

    @Override
    public List<LifeConfig> getConfigData() {
        return lifeConfigMapper.selectList(Wrappers.<LifeConfig>lambdaQuery());
    }

    @Override
    public void SaveInfo(String path, Integer id) {
        LifeConfig info = new LifeConfig();
        info.setConfigId(id);
        info.setStatus(2);
        info.setCreateTime(new Date());
        info.setImgUrl(path);
        lifeConfigMapper.insert(info);
    }
}
