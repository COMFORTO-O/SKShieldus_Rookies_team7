package com.example.shieldus.controller.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MyPageResponseDto {
    private String name; //이름
    private String email; //이메일
    private List<SolvedProblem> solvedProblems; // 맞춘문제 리스트

    // 풀었던 문제 내부 DTO
    @Getter
    @Builder
    public static class SolvedProblem {
        private String problemTitle; // 문제 제목
        private LocalDateTime completedAt; // 완료 날짜
    }
}
