package com.example.random.domain.repository.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.random.domain.entity.DateConfig;
import com.example.random.domain.repository.DateListRepository;
import com.example.random.interfaces.mapper.DateConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DateListRepositoryImpl implements DateListRepository {
    private final DateConfigMapper dateConfigMapper;

    @Override
    public List<DateConfig> getUserDateConfig(Long id) {
        return dateConfigMapper.selectList(Wrappers.<DateConfig>lambdaQuery()
                .eq(DateConfig::getPersonAlbumId, id));
    }

    @Override
    public DateConfig getDateConfigByIdAndYear(Long id, String year) {
        return dateConfigMapper.selectOne(Wrappers.<DateConfig>lambdaQuery()
                .eq(DateConfig::getPersonAlbumId, id)
                .eq(DateConfig::getDate, year));
    }

    @Override
    public void save(Long id, String year) {
        DateConfig info = new DateConfig();
        info.setDate(year);
        info.setPersonAlbumId(id);
        info.setCreateTime(new Date());
        dateConfigMapper.insert(info);
    }
}
