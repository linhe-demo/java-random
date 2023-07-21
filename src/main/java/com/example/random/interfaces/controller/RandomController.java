package com.example.random.interfaces.controller;


import com.example.random.domain.output.ApiResponse;
import com.example.random.interfaces.constant.Path;
import com.example.random.interfaces.controller.put.request.life.LifeRequest;
import com.example.random.interfaces.controller.put.response.life.LifeResponse;
import com.example.random.service.RandomWordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(Path.PREFIX_V1)
@RequiredArgsConstructor
public class RandomController {
    private final RandomWordsService randomWordsService;

    @PostMapping("life/moment")
    public ResponseEntity<ApiResponse<List<LifeResponse>>> randomWords(@RequestBody LifeRequest request) {
        List<LifeResponse> data = randomWordsService.getRandomWords(request);
        return ResponseEntity.ok(new ApiResponse<>(data));
    }
}
