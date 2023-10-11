package com.example.random.domain.repository;

import com.example.random.domain.entity.AlbumConfig;

import java.util.Date;
import java.util.List;

public interface AlbumConfigRepository {
    List<AlbumConfig> getAlbumConfig();

    int saveAlbumConfig(String name, String desc, Date date, Long id);
}
