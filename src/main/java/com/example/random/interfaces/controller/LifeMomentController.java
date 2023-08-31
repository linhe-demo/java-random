package com.example.random.interfaces.controller;


import com.example.random.domain.output.ApiResponse;
import com.example.random.interfaces.constant.Path;
import com.example.random.interfaces.controller.put.request.life.LifeRequest;
import com.example.random.interfaces.controller.put.response.config.ConfigResponse;
import com.example.random.interfaces.controller.put.response.life.LifeResponse;
import com.example.random.service.LifeMomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(Path.PREFIX_V1)
@RequiredArgsConstructor
public class LifeMomentController {
    private final LifeMomentService lifeMomentService;

    @PostMapping("life/moment")
    public ResponseEntity<ApiResponse<List<LifeResponse>>> momentData(@RequestBody LifeRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(lifeMomentService.momentData(request)));
    }

    @PostMapping("life/config")
    public ResponseEntity<ApiResponse<List<ConfigResponse>>> configList() {
        return ResponseEntity.ok(new ApiResponse<>(lifeMomentService.configData()));
    }
}
