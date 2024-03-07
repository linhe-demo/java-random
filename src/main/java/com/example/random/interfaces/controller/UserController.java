package com.example.random.interfaces.controller;

import com.example.random.annotation.UserLoginToken;
import com.example.random.domain.output.ApiResponse;
import com.example.random.interfaces.constant.Path;
import com.example.random.interfaces.controller.put.request.life.FellingRequest;
import com.example.random.interfaces.controller.put.response.user.UserFeatureResponse;
import com.example.random.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
}
