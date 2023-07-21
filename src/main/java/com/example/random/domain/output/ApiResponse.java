package com.example.random.domain.output;

public class ApiResponse<T> extends AbstractResponse {
    private  T data;

    public ApiResponse() {
    }

    public ApiResponse(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
