// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 변경 요약
// - 새로 추가된 DTO: 문제 상세, 테스트케이스, 풀이(Submission) 정보를 모두 담음
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

package com.example.shieldus.controller.dto;

import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemCode;
import com.example.shieldus.entity.problem.ProblemTestCase;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProblemDetailDto {
    private Long id;
    private String title;
    private String detail;      // 마크다운 문자열
    private CategoryDto category;    // enum name (예: “JAVA”)
    private Integer level;
    private String memberName;  // 작성자 이름
    private boolean solved;     // 현재 로그인 회원 기준으로 풀었는지 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 테스트케이스 목록
    private List<TestCaseInfoDto> testCase;


    // Member Submit Problem -> Problem Detail Dto
    public ProblemDetailDto(MemberSubmitProblem submitProblem) {
        Problem problem = submitProblem.getProblem();
        List<ProblemTestCase> testCases = problem.getTestCases();

        this.id = problem.getId();
        this.title = problem.getTitle();
        this.detail = problem.getDetail();
        this.level = problem.getLevel();
        this.memberName = problem.getMember().getName();
        this.solved = submitProblem.getPass();
        this.createdAt = problem.getCreatedAt();
        this.updatedAt = problem.getUpdatedAt();

        this.testCase = testCases.stream().map(ProblemDetailDto.TestCaseInfoDto::fromEntity).toList();
        this.category = ProblemDetailDto.CategoryDto.fromEntity(problem.getCategory());
    }


    // Member Submit Problem -> Problem Detail Dto
    public ProblemDetailDto(Problem problem, MemberSubmitProblem submitProblem) {
        List<ProblemTestCase> testCases = problem.getTestCases();

        this.id = problem.getId();
        this.title = problem.getTitle();
        this.detail = problem.getDetail();
        this.level = problem.getLevel();
        this.memberName = problem.getMember().getName();
        this.createdAt = problem.getCreatedAt();
        this.updatedAt = problem.getUpdatedAt();
        this.testCase = testCases.stream().map(ProblemDetailDto.TestCaseInfoDto::fromEntity).toList();
        this.category = ProblemDetailDto.CategoryDto.fromEntity(problem.getCategory());
        if(submitProblem != null){
            this.solved = submitProblem.getPass();
        }
    }

    // Problem -> Problem Detail Dto
    public ProblemDetailDto(Problem problem) {
        List<ProblemTestCase> testCases = problem.getTestCases();
        this.id = problem.getId();
        this.title = problem.getTitle();
        this.detail = problem.getDetail();
        this.level = problem.getLevel();
        this.memberName = problem.getMember().getName();
        this.createdAt = problem.getCreatedAt();
        this.updatedAt = problem.getUpdatedAt();
        this.testCase = testCases.stream().map(ProblemDetailDto.TestCaseInfoDto::fromEntity).toList();
        this.category = ProblemDetailDto.CategoryDto.fromEntity(problem.getCategory());
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TestCaseInfoDto {
        private Long id;
        private String input;
        private String output;

        public static TestCaseInfoDto fromEntity(ProblemTestCase testCase){
            return ProblemDetailDto.TestCaseInfoDto.builder()
                    .id(testCase.getId())
                    .input(testCase.getInput())
                    .output(testCase.getOutput())
                    .build();
        }
    }
    @Getter
    @Builder
    public static class CategoryDto {
        private Long id;
        private String code;
        private String description;

        public static CategoryDto fromEntity(ProblemCode problemCode){
            return new CategoryDto(problemCode.getId(), problemCode.getCode(), problemCode.getDescription());
        }
    }

    @Getter
    @Builder
    public static class SolutionInfoDto {
        private String memberName;      // 풀이한 회원의 이름
        private LocalDateTime completedAt;  // 풀이 완료 일시
    }


}