package com.example.shieldus.controller.dto;
import com.example.shieldus.entity.problem.ProblemTestCase;
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

        public static TestCaseResult fail(ProblemTestCase testCase, String errorMessage) {
            return TestCaseResult.builder()
                    .input(testCase.getInput())
                    .expectedOutput(testCase.getOutput())
                    .actualOutput(null) // 실패했으므로 실제 출력은 null
                    .isCorrect(false) // 실패했으므로 false
                    .error(errorMessage) // 에러 메시지 설정
                    .build();
        }
    }
}
