package com.example.shieldus.exception;

    /**
     * 애플리케이션 전역에서 발생 가능한 사용자 정의 예외
     * ErrorCode를 통해 일관된 예외 처리를 가능하게 한다.
     */

public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 예외 메시지 기본값 지정
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

