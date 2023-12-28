package com.example.random.domain.repository;

import com.example.random.domain.entity.AlbumConfig;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface AlbumConfigRepository {
    List<AlbumConfig> getAlbumConfig(Long id, Date beginDate, Date endDate);

    int saveAlbumConfig(String name, String desc, LocalDate date, Long id);

    AlbumConfig getAlbumConfigById(Integer configId);
}
