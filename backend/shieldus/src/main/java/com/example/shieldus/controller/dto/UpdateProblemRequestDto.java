// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 변경 요약
// - S7-32: ProblemRequestDto.Update → S7-33: UpdateProblemRequestDto
// - testCaseId 필드 nullable, 수정 vs. 신규 생성 분리
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

package com.example.shieldus.controller.dto;

import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProblemRequestDto {

    @NotNull(message = "문제 ID가 필요합니다.")
    private Long problemId;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "상세 설명은 필수입니다.")
    private String detail;

    @NotBlank(message = "카테고리를 입력하세요.")
    private ProblemCategoryEnum category;

    @NotNull(message = "난이도를 입력하세요.")
    private Integer level;

    @NotEmpty(message = "테스트 케이스를 한 개 이상 작성하세요.")
    private List<TestCaseDto> testCase;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCaseDto {
        /**
         * • 기존에 존재하는 테스트 케이스를 수정하려면 해당 testCaseId를 넘기고,
         * • 새롭게 추가하려면 testCaseId를 null로 둡니다.
         */
        private Long testCaseId;

        @NotBlank(message = "입력 예시는 필수입니다.")
        private String input;

        @NotBlank(message = "출력 예시는 필수입니다.")
        private String output;


        public Boolean isNullId(){
            return Objects.isNull(this.testCaseId) || this.testCaseId == 0L;
        }
    }
}