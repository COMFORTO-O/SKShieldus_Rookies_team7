package com.example.shieldus.config.exception.enumrate;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common errors - 공통으로 사용할 수 있는 일반적인 에러 코드
    RESOURCE_NOT_FOUND("%s not found with %s: %s", HttpStatus.NOT_FOUND),
    RESOURCE_DUPLICATE("%s already exists with %s: %s", HttpStatus.CONFLICT),
    RESOURCE_ALREADY_EXISTS("%s already exists: %s", HttpStatus.CONFLICT),

    // Student specific errors - 학생 관련 특수한 경우
    STUDENT_NUMBER_DUPLICATE("Student already exists with student number: %s", HttpStatus.CONFLICT),

    // StudentDetail specific errors - 학생 상세정보 관련 특수한 경우
    EMAIL_DUPLICATE("Student detail already exists with email: %s", HttpStatus.CONFLICT),
    PHONE_NUMBER_DUPLICATE("Student detail already exists with phone number: %s", HttpStatus.CONFLICT),

    // Book specific errors - 도서 관련 특수한 경우
    ISBN_DUPLICATE("Book already exists with ISBN: %s", HttpStatus.CONFLICT),

    // Department specific errors - 학과 관련 특수한 경우
    DEPARTMENT_CODE_DUPLICATE("Department already exists with code: %s", HttpStatus.CONFLICT),
    DEPARTMENT_NAME_DUPLICATE("Department already exists with name: %s", HttpStatus.CONFLICT),
    DEPARTMENT_HAS_STUDENTS("Cannot delete department with id: %s. It has %s students",
            HttpStatus.CONFLICT);

    private final String messageTemplate;
    private final HttpStatus httpStatus;

    // ... 가변적인 아규먼트 (Variable Arguments)
    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}