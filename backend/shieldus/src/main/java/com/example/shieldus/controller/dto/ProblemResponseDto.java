package com.example.shieldus.controller.dto;

import com.example.shieldus.controller.dto.member.MemberTempCodeResponseDto;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.member.MemberTempCode;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ProblemResponseDto {
    private Long id;
    private String title;
    private String detail;
    private ProblemCategoryEnum category;
    private Integer level;
    // member
    private String memberName;

    // memberSubmitProblem
    private Long submitProblemId;
    private Boolean solved;
    private LocalDateTime completeDate;


    // 문제 상세
    public static ProblemResponseDto fromProblem(Problem problem) {
        return ProblemResponseDto.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .detail(problem.getDetail())
                .category(problem.getCategory())
                .level(problem.getLevel())
                .memberName(problem.getMember().getName())
                .build();
    }

    // 사용자 푼 문제 조회용 dto
    public ProblemResponseDto(Long id, String title, String detail, ProblemCategoryEnum category, Integer level,
                              Long submitProblemId, Boolean solved, LocalDateTime completeDate) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.category = category;
        this.level = level;
        this.submitProblemId = submitProblemId;
        this.solved = solved;
        this.completeDate = completeDate;
    }


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
                    .category(problem.getCategory())
                    .level(problem.getLevel())
                    .submitProblemId(submitProblem.getId())
                    .solved(submitProblem.getPass())
                    .completeDate(submitProblem.getCompleteDate())
                    .build();
            this.tempCode = MemberTempCodeResponseDto.fromEntity(code);
        }
    }


}