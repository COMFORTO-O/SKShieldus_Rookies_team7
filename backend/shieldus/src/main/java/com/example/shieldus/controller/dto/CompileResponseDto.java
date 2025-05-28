package com.example.shieldus.controller.dto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CompileResponseDto {
    private int passedCount;
    private int totalCount;
    private List<TestCaseResult> testCaseResults;

    @Getter
    @Builder
    public static class TestCaseResult {
        private String input;
        private String expectedOutput;
        private String actualOutput;
        private boolean isCorrect;
        private String error; // 런타임 에러 등
    }
}
