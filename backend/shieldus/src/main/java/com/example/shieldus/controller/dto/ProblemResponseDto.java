package com.example.shieldus.controller.dto;

import com.example.shieldus.controller.dto.member.MemberTempCodeResponseDto;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.member.MemberTempCode;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemCode;
import com.example.shieldus.entity.problem.ProblemTestCase;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import com.example.shieldus.entity.problem.enumration.ProblemLanguageEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProblemResponseDto {
    private Long id;
    private String title;
    private String detail;
    private ProblemCodeDto category;
    private Integer level;
    private LocalDateTime createdAt;
    private ProblemLanguageEnum language;

    // member
    private String memberName;

    // memberSubmitProblem
    private Long submitProblemId;
    private Boolean solved;
    private LocalDateTime completedAt;

    // passRate
    private Double passRate;


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
                              Long submitProblemId, Boolean solved, LocalDateTime completedAt) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.category = ProblemCodeDto.fromEntity(problemCode); // ProblemCategoryEnum → ProblemCodeDto로 변경
        this.level = level;
        this.submitProblemId = submitProblemId;
        this.solved = solved;
        this.completedAt = completedAt;
    }


    public ProblemResponseDto(Long id, String title, String detail, Integer level, LocalDateTime createdAt,
                              Long problemCodeId, String problemCode, String problemDescription, ProblemLanguageEnum language) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.level = level;
        this.createdAt = createdAt;
        this.category = new ProblemCodeDto(problemCodeId, problemCode, problemDescription);
        this.language = language;

    }
    // pass 관련 값 제작
    public void setPass(Long successCount, Long count) {
        if (count == null || count == 0) {
            this.passRate = 0.0;
        } else {
            double rate = successCount.doubleValue() / count;
            this.passRate = Math.round(rate * 100.0) / 100.0; // 소수점 둘째 자리까지 반올림
        }
    }
    // 문제 상세
    public static ProblemResponseDto fromProblem(Problem problem) {
        return ProblemResponseDto.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .detail(problem.getDetail())
                .category(ProblemCodeDto.fromEntity(problem.getCategory())) // ProblemCategoryEnum → ProblemCodeDto로 변경
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
                    .category(ProblemCodeDto.fromEntity(problem.getCategory())) // ProblemCategoryEnum → ProblemCodeDto로 변경
                    .level(problem.getLevel())
                    .submitProblemId(submitProblem.getId())
                    .solved(submitProblem.getPass())
                    .completedAt(submitProblem.getCompletedAt())
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



    // 정답률 계산을 위한 클래스
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Pass{
        public Long memberId;
        public Long problemId;
        public Boolean isPass;
        public LocalDateTime completedAt;

        public Pass(Long memberId, Long problemId, Integer isPass, LocalDateTime completedAt) {
            this.memberId = memberId;
            this.problemId = problemId;
            if(isPass != null && isPass > 0 ){
                this.isPass = true;
                this.completedAt = completedAt;
            }
        }
    }

}
