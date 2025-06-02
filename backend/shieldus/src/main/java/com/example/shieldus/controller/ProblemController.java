package com.example.shieldus.controller;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.controller.dto.*;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import com.example.shieldus.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                userDetails.getMemberId(), category, level, title, status, pageable);
        return ResponseDto.success(page);
    }

    @PostMapping("/create") // 문제생성
    public ResponseDto<ProblemResponseDto> createProblem(
            @RequestBody ProblemRequestDto.Create dto,
            @AuthenticationPrincipal MemberUserDetails userDetails) {
        Long problemId = problemService.createProblem(dto, userDetails.getMemberId());
        return ResponseDto.success(ProblemResponseDto.builder().id(problemId).build());
    }

    @DeleteMapping("/delete/{id}") // 문제삭제
    public ResponseDto<String> deleteProblem(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails) {
        problemService.deleteProblem(id, userDetails.getMemberId());
        return ResponseDto.success("문제가 성공적으로 삭제되었습니다.");
    }

    @PostMapping("/update/{id}") // 문제정보업데이트
    public ResponseDto<ProblemResponseDto> updateProblem(
            @RequestBody ProblemRequestDto.Update dto,
            @AuthenticationPrincipal MemberUserDetails userDetails) {
        Long problemId = problemService.updateProblem(dto, userDetails.getMemberId());
        return ResponseDto.success(ProblemResponseDto.builder().id(problemId).build());
    }

    @GetMapping("/detail/{problemId}") // 문제상세정보조회
    public ResponseDto<ProblemResponseDto.Detail> getProblemDetail(
            @PathVariable Long problemId,
            @AuthenticationPrincipal MemberUserDetails userDetails) {
        ProblemResponseDto.Detail problemDetail = problemService.getProblemDetail(problemId);
        return ResponseDto.success(problemDetail);
    }

    @GetMapping("/detail/{problemId}/test-case") // 문제의 테스트 케이스 목록조회
    public ResponseDto<List<ProblemTestCaseResponseDto.Detail>> getProblemTestDetail(
            @RequestParam(required = false) Boolean test,
            @PathVariable Long problemId,
            @AuthenticationPrincipal MemberUserDetails userDetails) {
        List<ProblemTestCaseResponseDto.Detail> testCaseList =
                problemService.getProblemTestCaseListByProblemId(test, problemId);
        return ResponseDto.success(testCaseList);
    }

    @GetMapping("/detail/{problemId}/solved")
    public ResponseDto<Page<SolvedProblemResponseDto>> getProblemSolvedList(
            @PathVariable Long problemId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            @AuthenticationPrincipal MemberUserDetails userDetails) {

        Page<SolvedProblemResponseDto> solvedProblems =
                problemService.getSolvedProblemList(problemId, userDetails.getMemberId(), pageable);
        return ResponseDto.success(solvedProblems);
    }
}