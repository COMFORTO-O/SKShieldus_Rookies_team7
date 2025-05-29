package com.example.shieldus.exception;

import com.example.shieldus.controller.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검사 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = Optional.ofNullable(ex.getBindingResult().getFieldError())
                .map(fe -> fe.getDefaultMessage())
                .orElse("유효성 검사 오류가 발생했습니다.");

        log.warn("Validation failed: {}", errorMessage);
        ResponseDto<Void> response = ResponseDto.error(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.VALIDATION_FAILED.getCode(), // "VAL422"
                errorMessage
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 잘못된 인자 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        ResponseDto<Void> response = ResponseDto.error(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.INVALID_REQUEST.getCode(), // "REQ400"
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 커스텀 예외 처리 (ErrorCode 활용)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDto<Void>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        // loggable 여부에 따라 로깅 레벨 분기
        if (errorCode.isLoggable()) {
            log.error("CustomException: [{}] {}", errorCode.getCode(), errorCode.getMessage(), ex);
        } else {
            log.warn("CustomException: [{}] {}", errorCode.getCode(), errorCode.getMessage());
        }

        ResponseDto<Void> response = ResponseDto.error(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage()
        );
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    // 미처리 예외 처리 (전역 예외)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ResponseDto<Void> response = ResponseDto.error(
                errorCode.getStatus(),
                errorCode.getCode(), // "SYS500"
                errorCode.getMessage()
        );
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}

