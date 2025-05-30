package com.example.shieldus.controller;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.controller.dto.ResponseDto;
import com.example.shieldus.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
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

    // 문제 만들기
    @GetMapping("/create")
    public ResponseDto<String> createProblem(@AuthenticationPrincipal MemberUserDetails userDetails) {
        return ResponseDto.success("ok");
    }

    // 문제 삭제
    @GetMapping("/delete/{id}")
    public ResponseDto<String> deleteProblem(@AuthenticationPrincipal MemberUserDetails userDetails) {
        return ResponseDto.success("ok");
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