package com.example.random.interfaces.controller.put.response.user;

import com.example.random.domain.value.BabyLifeImg;
import lombok.Data;

import java.util.List;

@Data
public class BabyHighlightResponse {
    private List<BabyLifeImg> data;

    private List<String> list;
}
