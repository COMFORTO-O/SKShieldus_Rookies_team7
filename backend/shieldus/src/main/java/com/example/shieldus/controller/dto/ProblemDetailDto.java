// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 변경 요약
// - 새로 추가된 DTO: 문제 상세, 테스트케이스, 풀이(Submission) 정보를 모두 담음
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

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

    @Getter
    @Builder
    public static class SolutionInfoDto {
        private String memberName;      // 풀이한 회원의 이름
        private LocalDateTime completeDate;  // 풀이 완료 일시
    }
}