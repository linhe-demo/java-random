package com.example.random.interfaces.controller.put.request.album;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class AlbumConfigAddRequest {
    private String name;
    private String desc;
    private LocalDate date;
}
