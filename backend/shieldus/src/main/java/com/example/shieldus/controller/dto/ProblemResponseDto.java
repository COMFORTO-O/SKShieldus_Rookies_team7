package com.example.shieldus.controller.dto;

import com.example.shieldus.controller.dto.member.MemberTempCodeResponseDto;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.member.MemberTempCode;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemCode;
import com.example.shieldus.entity.problem.ProblemTestCase;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProblemResponseDto {
    private Long id;
    private String title;
    private String detail;
    private ProblemCodeDto category;
    private Integer level;
    // member
    private String memberName;

    // memberSubmitProblem
    private Long submitProblemId;
    private Boolean solved;
    private LocalDateTime completeDate;

    // QueryDSL에서 사용할 생성자 추가
    public ProblemResponseDto(Long id, String title, String detail,
                              ProblemCode problemCode, Integer level,
                              String memberName, Boolean solved) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.category = ProblemCodeDto.fromEntity(problemCode); // ProblemCategoryEnum → ProblemCodeDto로 변경
        this.level = level;
        this.memberName = memberName;
        this.solved = solved;
    }

    // 사용자 푼 문제 조회용 생성자
    public ProblemResponseDto(Long id, String title, String detail,
                              ProblemCode problemCode, Integer level,
                              Long submitProblemId, Boolean solved, LocalDateTime completeDate) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.category = ProblemCodeDto.fromEntity(problemCode); // ProblemCategoryEnum → ProblemCodeDto로 변경
        this.level = level;
        this.submitProblemId = submitProblemId;
        this.solved = solved;
        this.completeDate = completeDate;
    }

    // 문제 상세
    public static ProblemResponseDto fromProblem(Problem problem) {
        return ProblemResponseDto.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .detail(problem.getDetail())
                .category(ProblemCodeDto.fromEntity(problem.getProblemCode())) // ProblemCategoryEnum → ProblemCodeDto로 변경
                .level(problem.getLevel())
                .memberName(problem.getMember().getName())
                .build();
    }

    // 문제 + 테스트케이스 포함된 상세 DTO
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Detail {
        public ProblemResponseDto detail;
        public List<ProblemTestCaseResponseDto.Detail> testCase;

        public static Detail fromProblem(Problem problem) {
            return new Detail(
                    ProblemResponseDto.fromProblem(problem),
                    problem.getTestCases().stream().map(ProblemTestCaseResponseDto.Detail::fromEntity).toList());
        }
    }

    // 사용자가 푼 문제 DTO
    @Getter
    @Setter
    public static class SolvedProblem {
        private ProblemResponseDto problem;
        private MemberTempCodeResponseDto tempCode;

        public SolvedProblem(MemberTempCode code){
            MemberSubmitProblem submitProblem = code.getMemberSubmitProblem();
            Problem problem = submitProblem.getProblem();
            this.problem = ProblemResponseDto.builder()
                    .id(problem.getId())
                    .title(problem.getTitle())
                    .detail(problem.getDetail())
                    .category(ProblemCodeDto.fromEntity(problem.getProblemCode())) // ProblemCategoryEnum → ProblemCodeDto로 변경
                    .level(problem.getLevel())
                    .submitProblemId(submitProblem.getId())
                    .solved(submitProblem.getPass())
                    .completeDate(submitProblem.getCompleteDate())
                    .build();
            this.tempCode = MemberTempCodeResponseDto.fromEntity(code);
        }
    }

    // DTO 내부에 ProblemCodeDto 추가
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProblemCodeDto {
        private Long id;
        private String code;
        private String description;

        public static ProblemCodeDto fromEntity(ProblemCode problemCode) {
            return ProblemCodeDto.builder()
                    .id(problemCode.getId())
                    .code(problemCode.getCode())
                    .description(problemCode.getDescription())
                    .build();
        }
    }
}
