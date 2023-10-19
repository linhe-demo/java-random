package com.example.random.domain.repository;

import com.example.random.domain.entity.DateConfig;

import java.util.List;

public interface DateListRepository {
    List<DateConfig> getUserDateConfig(Long id);
    DateConfig getDateConfigByIdAndYear(Long id, String year);
    void save(Long id, String year);
}
