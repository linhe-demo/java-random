package com.example.random.interfaces.controller;


import com.example.random.domain.output.ApiResponse;
import com.example.random.interfaces.constant.Path;
import com.example.random.interfaces.controller.put.request.album.AlbumConfigAddRequest;
import com.example.random.interfaces.controller.put.request.life.LifeRequest;
import com.example.random.interfaces.controller.put.request.user.AlbumListRequest;
import com.example.random.interfaces.controller.put.request.user.DateListRequest;
import com.example.random.interfaces.controller.put.request.user.RegisterRequest;
import com.example.random.interfaces.controller.put.request.user.UserRequest;
import com.example.random.interfaces.controller.put.response.config.ConfigResponse;
import com.example.random.interfaces.controller.put.response.life.LifeResponse;
import com.example.random.interfaces.controller.put.response.user.AlbumResponse;
import com.example.random.interfaces.controller.put.response.user.DateListResponse;
import com.example.random.interfaces.controller.put.response.user.RegisterResponse;
import com.example.random.interfaces.controller.put.response.user.UserResponse;
import com.example.random.service.LifeMomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping(Path.PREFIX_V1)
@RequiredArgsConstructor
public class LifeMomentController {
    private final LifeMomentService lifeMomentService;

    @PostMapping("life/moment")
    public ResponseEntity<ApiResponse<List<LifeResponse>>> momentData(@RequestBody LifeRequest request, HttpServletRequest ip) {
        return ResponseEntity.ok(new ApiResponse<>(lifeMomentService.momentData(request, ip)));
    }

    @PostMapping("life/config")
    public ResponseEntity<ApiResponse<List<ConfigResponse>>> configList() {
        return ResponseEntity.ok(new ApiResponse<>(lifeMomentService.configData()));
    }

    @PostMapping("user/login")
    public ResponseEntity<ApiResponse<UserResponse>> userLogin(@RequestBody UserRequest request, HttpServletRequest ip) {
        return ResponseEntity.ok(new ApiResponse<>(lifeMomentService.checkLogin(request, ip)));
    }

    @PostMapping("user/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> userRegister(@RequestBody RegisterRequest request, HttpServletRequest ip) {
        return ResponseEntity.ok(new ApiResponse<>(lifeMomentService.userRegister(request, ip)));
    }

    @PostMapping("album/list")
    public ResponseEntity<ApiResponse<List<AlbumResponse>>> AlbumList(@RequestBody AlbumListRequest request, HttpServletRequest ip) {
        return ResponseEntity.ok(new ApiResponse<>(lifeMomentService.getAlbumList(request, ip)));
    }

    @PostMapping("auto/login")
    public ResponseEntity<ApiResponse<Boolean>> autoLogin(HttpServletRequest ip) {
        return ResponseEntity.ok(new ApiResponse<>(lifeMomentService.autoLogin(ip)));
    }

    @PostMapping("image/upload")
    public ResponseEntity<ApiResponse<Boolean>> uploadFile(@RequestParam("files") MultipartFile[] files, @RequestParam("id") Integer id, HttpServletRequest ip) {
        return ResponseEntity.ok(new ApiResponse<>(lifeMomentService.upload(files, id, ip)));
    }

    @PostMapping("album/add")
    public ResponseEntity<ApiResponse<Boolean>> albumAdd(@RequestBody AlbumConfigAddRequest request, HttpServletRequest ip) {
        return ResponseEntity.ok(new ApiResponse<>(lifeMomentService.albumAdd(request, ip)));
    }

    @PostMapping("date/list")
    public ResponseEntity<ApiResponse<List<String>>> dateList(@RequestBody DateListRequest request, HttpServletRequest ip) {
        return ResponseEntity.ok(new ApiResponse<>(lifeMomentService.dateList(request, ip)));
    }
}
