package com.example.shieldus.controller.dto;


import com.example.shieldus.entity.problem.ProblemTestCase;
import lombok.*;

@Getter
public class ProblemTestCaseResponseDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail{
        private Long testCaseId;
        private String input;
        private String output;
        private Boolean isTestCase;

        public static Detail fromEntity(ProblemTestCase problemTestCase) {
            return new Detail(problemTestCase.getId(),
                    problemTestCase.getInput(),
                    problemTestCase.getOutput(),
                    problemTestCase.getIsTestCase());
        }
    }
}
