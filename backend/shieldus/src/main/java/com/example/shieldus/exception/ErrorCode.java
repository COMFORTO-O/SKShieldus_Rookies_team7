package com.example.shieldus.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.hibernate.Internal;
import org.springframework.http.HttpStatus;
/**
 * 전역 에러 코드 Enum - 간결 버전
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

//    REQ	Request 오류	400 Bad Request	REQ400	잘못된 파라미터, 필드 누락, JSON 파싱 실패 등 클라이언트 요청 문제
//    USR	사용자 오류	404 Not Found	USR404	사용자를 찾을 수 없음
//    AUTH	인증 오류	401 Unauthorized	AUTH401	인증되지 않은 사용자 (로그인 안 함 등)
//    PERM	권한 오류	403 Forbidden	PERM403	접근 권한이 없음 (로그인은 했지만 금지된 리소스 접근 시도 등)
//    RES	리소스 충돌 오류	409 Conflict	RES409	중복된 리소스 생성 시도, 리소스 상태 충돌 등
//    VAL	유효성 검사 실패	422 Unprocessable Entity	VAL422	서버는 이해했지만 요청 데이터가 유효하지 않음
//    SYS	시스템 오류	500 Internal Server Error	SYS500	예기치 못한 서버 오류, NPE 등
//    DB	DB 오류	500 Internal Server Error	DB500	쿼리 실패, DB 연결 문제 등

    INVALID_REQUEST("REQ400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", false),
    USER_NOT_FOUND("USR404", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", false),
    UNAUTHORIZED("AUTH401", HttpStatus.UNAUTHORIZED, "인증이 필요합니다.", false),
    AUTHENTICATION_FAILED("AUTH401", HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다..", false),
    FORBIDDEN("PERM403", HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", false),
    DUPLICATE_RESOURCE("RES409", HttpStatus.CONFLICT, "이미 존재하는 리소스입니다.", false),
    VALIDATION_FAILED("VAL422", HttpStatus.UNPROCESSABLE_ENTITY, "요청 데이터의 유효성 검사에 실패했습니다.", false),
    DATABASE_ERROR("DB500", HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 처리 중 오류가 발생했습니다.", true),
    INTERNAL_SERVER_ERROR("SYS500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.", true),
    PROBLEM_NOT_FOUND("PRB404", HttpStatus.NOT_FOUND, "문제를 찾을 수 없습니다.", false),
    JUDGE_SERVER_ERROR("JUD502", HttpStatus.BAD_GATEWAY, "채점 서버 연결에 실패했습니다.", true),
    JSON_PARSING_ERROR("JSON400", HttpStatus.BAD_REQUEST, "JSON 처리 중 오류가 발생했습니다.", false),
    UNSUPPORTED_LANGUAGE("LANG400", HttpStatus.BAD_REQUEST, "지원하지 않는 프로그래밍 언어입니다.", false);

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
    private final boolean loggable;


    public int getStatus() {
        return httpStatus.value();
    }
}


