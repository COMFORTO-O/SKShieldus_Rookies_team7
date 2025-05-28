package com.example.shieldus.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDto<T> {
    private int status;
    private String message;
    private T data;

    // 성공 응답 생성 메서드
    public static <T> ResponseDto<T> success(T data) {
        return ResponseDto.<T>builder()
                .status(200)
                .message("success")
                .data(data)
                .build();
    }

    // 실패 응답 생성 메서드
    public static <T> ResponseDto<T> error(int status, String message) {
        return ResponseDto.<T>builder()
                .status(status)
                .message(message)
                .data(null)
                .build();
    }
}
