package com.example.shieldus.controller.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MyPageResponseDto {
    private String name;
    private String email;
    private List<SolvedProblem> solvedProblems; // 풀었던 문제 리스트

    // 풀었던 문제 내부 DTO
    @Getter
    @Builder
    public static class SolvedProblem {
        private String problemTitle; // 문제 제목
        private LocalDateTime completeDate; // 완료 날짜
    }
}
