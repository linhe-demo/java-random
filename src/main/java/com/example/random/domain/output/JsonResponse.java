package com.example.random.domain.output;

import lombok.Data;

@Data
public class JsonResponse<T> {
    Integer code;

    String message;

    T data;


    public JsonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public JsonResponse(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public JsonResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
