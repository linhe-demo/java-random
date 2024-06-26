package com.example.random.domain.repository.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.random.domain.entity.AlbumConfig;
import com.example.random.domain.entity.DateConfig;
import com.example.random.domain.repository.AlbumConfigRepository;
import com.example.random.interfaces.mapper.AlbumConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AlbumConfigRepositoryImpl implements AlbumConfigRepository {
    private final AlbumConfigMapper albumConfigMapper;

    @Override
    @DS("life")
    public List<AlbumConfig> getAlbumConfig(Long id, Date beginDate, Date endDate) {
        return albumConfigMapper.selectList(Wrappers.<AlbumConfig>lambdaQuery()
                .eq(AlbumConfig::getStatus, 1)
                .eq(AlbumConfig::getPersonAlbumId, id)
                .ge(beginDate != null, AlbumConfig::getDate, beginDate)
                .le(endDate != null, AlbumConfig::getDate, endDate)
                .last(" order by `date` ASC "));
    }

    @Override
    @DS("life")
    public int saveAlbumConfig(String name, String desc, LocalDate date, Long id) {
        AlbumConfig info = new AlbumConfig();
        info.setTitle(name);
        info.setDesc(desc);
        info.setDate(date);
        info.setPersonAlbumId(id);
        info.setCreateTime(new Date());
        info.setStatus(1);
        return albumConfigMapper.insert(info);
    }

    @Override
    public AlbumConfig getAlbumConfigById(String configId) {
        return albumConfigMapper.selectOne(Wrappers.<AlbumConfig>lambdaQuery()
                .eq(AlbumConfig::getId, configId));
    }

    @Override
    public List<AlbumConfig> getAlbumConfigByPersonId(long id) {
        return albumConfigMapper.selectList(Wrappers.<AlbumConfig>lambdaQuery()
                .eq(AlbumConfig::getPersonAlbumId, id));
    }
}
