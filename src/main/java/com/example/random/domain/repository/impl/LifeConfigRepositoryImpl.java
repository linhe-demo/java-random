package com.example.random.domain.repository.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.random.domain.entity.LifeConfig;
import com.example.random.domain.repository.LifeConfigRepository;
import com.example.random.interfaces.mapper.LifeConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LifeConfigRepositoryImpl implements LifeConfigRepository {
    private final LifeConfigMapper lifeConfigMapper;

    @Override
    public List<LifeConfig> getLifeConfigData(Integer num) {
        return lifeConfigMapper.selectList(Wrappers.<LifeConfig>lambdaQuery()
                .last(String.format(" ORDER BY RAND() LIMIT %s ", num)));
    }
}
