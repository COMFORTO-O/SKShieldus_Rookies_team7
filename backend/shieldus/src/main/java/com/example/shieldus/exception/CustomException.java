package com.example.shieldus.exception;


    // 애플리케이션 전역에서 발생 가능한 사용자 정의 예외
    // * ErrorCode를 통해 일관된 예외 처리를 가능하게 한다.


public class CustomException extends RuntimeException {

    // 예외 코드(Enum) 객체, 에러 메시지, 상태 코드, 로그 여부 등의 정보를 담고 있음
    private final ErrorCode errorCode;


    // 기본 생성자 - cause 없이 CustomException을 생성할 경우 사용
    // @param errorCode 발생한 에러에 해당하는 ErrorCode Enum 값

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 예외 메시지 기본값 지정
        this.errorCode = errorCode;
    }
    //  * 예외 체이닝을 위한 생성자 - 원인 예외(cause)를 함께 전달할 경우 사용
    //  * <p>이 생성자를 사용하면 예외가 발생한 근본적인 원인을 함께 저장할 수 있어,
    //  * 로깅 및 디버깅 시 더 많은 정보를 확인할 수 있습니다.
    //  * @param errorCode 발생한 에러에 해당하는 ErrorCode Enum 값
    //  * @param cause     이 예외를 발생시킨 원인 예외 객체
    public CustomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

