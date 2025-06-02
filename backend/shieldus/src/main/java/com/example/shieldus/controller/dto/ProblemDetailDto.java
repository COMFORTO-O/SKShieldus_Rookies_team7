package com.example.shieldus.controller.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProblemDetailDto {
    private Long id;
    private String title;
    private String detail;      // 마크다운 문자열
    private String category;    // enum name (예: “JAVA”)
    private Integer level;
    private String memberName;  // 작성자 이름
    private boolean solved;     // 현재 로그인 회원 기준으로 풀었는지 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 테스트케이스 목록
    private List<TestCaseInfoDto> testCase;

    @Getter
    @Builder
    public static class TestCaseInfoDto {
        private Long id;
        private String input;
        private String output;
    }
}