package com.example.random.interfaces.controller;

import com.example.random.annotation.UserLoginToken;
import com.example.random.domain.output.ApiResponse;
import com.example.random.interfaces.constant.Path;
import com.example.random.interfaces.controller.put.request.user.BabyHighlightRequest;
import com.example.random.interfaces.controller.put.request.user.BabyLifeRequest;
import com.example.random.interfaces.controller.put.request.user.DateListRequest;
import com.example.random.interfaces.controller.put.response.user.BabyHighlightResponse;
import com.example.random.interfaces.controller.put.response.user.BabyLifeResponse;
import com.example.random.interfaces.controller.put.response.user.UserFeatureResponse;
import com.example.random.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(Path.PREFIX_V1)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @UserLoginToken
    @PostMapping("feature/info")
    public ResponseEntity<ApiResponse<UserFeatureResponse>> addFelling(HttpServletRequest ip) {
        return ResponseEntity.ok(new ApiResponse<>(userService.getFeatureInfo(ip)));
    }

    @UserLoginToken
    @PostMapping("baby/upload")
    public ResponseEntity<ApiResponse<Boolean>> uploadFile(@RequestParam("name") String name, @RequestParam("desc") String desc, @RequestParam("date") String date, @RequestParam("files") MultipartFile[] files, HttpServletRequest ip) {
        return ResponseEntity.ok(new ApiResponse<>(userService.babyUpload(name, desc, date, files, ip)));
    }

    @UserLoginToken
    @PostMapping("baby/life")
    public ResponseEntity<ApiResponse<List<BabyLifeResponse>>> babyLife() {
        return ResponseEntity.ok(new ApiResponse<>(userService.babyLife()));
    }

    @UserLoginToken
    @PostMapping("baby/highlight")
    public ResponseEntity<ApiResponse<BabyHighlightResponse>> babyHighlight(@RequestBody BabyHighlightRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(userService.babyHighlight(request.getId())));
    }

}
