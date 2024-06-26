package com.example.random.domain.repository;

import com.example.random.domain.entity.AlbumConfig;
import com.example.random.domain.entity.DateConfig;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface AlbumConfigRepository {
    List<AlbumConfig> getAlbumConfig(Long id, Date beginDate, Date endDate);

    int saveAlbumConfig(String name, String desc, LocalDate date, Long id);

    AlbumConfig getAlbumConfigById(String configId);

    List<AlbumConfig> getAlbumConfigByPersonId(long id);
}
