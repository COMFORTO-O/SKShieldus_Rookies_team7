// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 변경 요약
// - S7-32: ProblemRequestDto.Create → S7-33: CreateProblemRequestDto
// - validation 에 노출되는 각 필드 메시지 갱신
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

package com.example.shieldus.controller.dto;

import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemTestCase;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
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
    private String detail;      // 마크다운 문자열

    @NotBlank(message = "카테고리를 입력하세요.")
    private ProblemCategoryEnum category;    // “JAVA”, “PYTHON”, …

    @NotNull(message = "난이도를 입력하세요.")
    private Integer level;

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

        public ProblemTestCase toEntity(Problem problem) {
            return ProblemTestCase.builder()
                    .problem(problem)
                    .isTestCase(true)
                    .input(this.input)
                    .output(this.output)
                    .build();
        }
    }
}