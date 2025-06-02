package com.example.shieldus.controller.dto;

import com.example.shieldus.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDto<T> {
    private int status;
    private String code;
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
    public static <T> ResponseDto<T> error(int status, String code,String message) {
        return ResponseDto.<T>builder()
                .status(status)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ResponseDto<T> error(ErrorCode errorCode) {
        return ResponseDto.<T>builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(null)
                .build();
    }
}
