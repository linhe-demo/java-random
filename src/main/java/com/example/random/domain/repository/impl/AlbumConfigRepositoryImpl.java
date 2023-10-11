package com.example.random.domain.repository.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.random.domain.entity.AlbumConfig;
import com.example.random.domain.repository.AlbumConfigRepository;
import com.example.random.interfaces.mapper.AlbumConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AlbumConfigRepositoryImpl implements AlbumConfigRepository {
    private final AlbumConfigMapper albumConfigMapper;

    @Override
    @DS("life")
    public List<AlbumConfig> getAlbumConfig(Long id) {
        return albumConfigMapper.selectList(Wrappers.<AlbumConfig>lambdaQuery()
                .eq(AlbumConfig::getStatus, 1)
                .eq(AlbumConfig::getPersonAlbumId, id));
    }

    @Override
    @DS("life")
    public int saveAlbumConfig(String name, String desc, Date date, Long id) {
        AlbumConfig info = new AlbumConfig();
        info.setTitle(name);
        info.setDesc(desc);
        info.setDate(date);
        info.setPersonAlbumId(id);
        info.setCreateTime(new Date());
        info.setStatus(1);
        return albumConfigMapper.insert(info) ;
    }
}
