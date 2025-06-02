package com.example.shieldus.controller;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.controller.dto.ResponseDto;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import com.example.shieldus.service.problem.ProblemService;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public ResponseDto<Page<ProblemResponseDto>> getProblems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            @AuthenticationPrincipal MemberUserDetails userDetails) {

        Page<ProblemResponseDto> page = problemService.getFilteredProblems(
                userDetails.getMemberId(), category, level, title, status, pageable
        );

        return ResponseDto.success(page);
    }

    // 문제 생성

    @Getter // 이 어노테이션이 있어야 getTitle() 등이 자동 생성됨
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProblemCreateRequest {
        private String title;
        private String detail;
        private ProblemCategoryEnum category;
        private Integer level;
    }

    @PostMapping("/create")
    public ResponseDto<Problem> createProblem(
            @RequestBody ProblemCreateRequest request,
            @AuthenticationPrincipal MemberUserDetails userDetails) {

        Problem createdProblem = problemService.createProblem(
                userDetails.getMemberId(),
                request.getTitle(),
                request.getDetail(),
                request.getCategory(),
                request.getLevel()
        );

        return ResponseDto.success(createdProblem);
    }

    // 문제 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseDto<String> deleteProblem(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails) {
        problemService.deleteProblem(id, userDetails.getMemberId());
        return ResponseDto.success("문제가 성공적으로 삭제되었습니다.");
    }

    // 문제 업데이트
    @GetMapping("/update{id}")
    public ResponseDto<String> updateProblem(@AuthenticationPrincipal MemberUserDetails userDetails) {
        return ResponseDto.success("ok");
    }
    // 문제 상세정보
    @GetMapping("/detail/{id}")
    public ResponseDto<String> getProblemDetail(@AuthenticationPrincipal MemberUserDetails userDetails) {
        return ResponseDto.success("ok");
    }

    // 문제 답변 리스트
    @GetMapping("/detail/{id}/solved")
    public ResponseDto<String> getProblemSolvedList(@AuthenticationPrincipal MemberUserDetails userDetails) {
        return ResponseDto.success("ok");
    }
}