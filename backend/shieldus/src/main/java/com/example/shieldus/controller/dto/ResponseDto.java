package com.example.shieldus.controller.dto;

import com.example.shieldus.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ResponseDto<T> {
    private int status;
    private String code;
    private String message;
    private T data;

    // 성공 응답
    public static <T> ResponseDto<T> success(T data) {
        return ResponseDto.<T>builder()
                //.status(200) <-- 기존코드
                .status(HttpStatus.OK.value()) // 200 -> HttpStatus상수활용(가독성향상)
                .message("success")
                .data(data)
                .build();
    }

    // 실패 응답 (직접 status, code, message 전달)
    public static <T> ResponseDto<T> error(int status, String code, String message) {
        return ResponseDto.<T>builder()
                .status(status)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    // 🔧 실패 응답 (ErrorCode enum 전달 시 편의용)
    public static <T> ResponseDto<T> error(ErrorCode errorCode) {
        return ResponseDto.<T>builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(null)
                .build();
    }
}