package com.example.shieldus.exception;

import com.example.shieldus.controller.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 예: 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDto<?> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseDto.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    // 예: IllegalArgumentException 등 커스텀 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseDto<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseDto.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    // 예: 그 외 모든 에러
    @ExceptionHandler(Exception.class)
    public ResponseDto<?> handleException(Exception ex) {
        return ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다.");
    }
}

