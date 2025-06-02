package com.example.shieldus.controller.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProblemRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "상세 설명은 필수입니다.")
    private String detail;      // 마크다운 문자열(↔ Problem.detail 테이블 컬럼)

    @NotBlank(message = "카테고리를 입력하세요.")
    private String category;    // “JAVA”, “PYTHON”, …

    @NotNull(message = "난이도를 입력하세요.")
    private Integer level;

    // 테스트 케이스를 한 개 이상 작성해야 함
    @NotEmpty(message = "테스트 케이스를 한 개 이상 등록하세요.")
    private List<TestCaseDto> testCase;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCaseDto {
        @NotBlank(message = "테스트 입력값은 필수입니다.")
        private String input;

        @NotBlank(message = "테스트 출력값은 필수입니다.")
        private String output;
    }
}