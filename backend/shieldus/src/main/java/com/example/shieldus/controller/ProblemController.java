package com.example.shieldus.controller;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.controller.dto.ProblemRequestDto;
import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.controller.dto.ProblemTestCaseResponseDto;
import com.example.shieldus.controller.dto.ResponseDto;
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
                userDetails.getMemberId(), category, level, title, status, pageable
        );

        return ResponseDto.success(page);
    }

    // 문제 만들기
    @PostMapping("/create")
    public ResponseDto<ProblemResponseDto> createProblem(@RequestBody ProblemRequestDto.Create dto, @AuthenticationPrincipal MemberUserDetails userDetails) {
        Long problemId = problemService.createProblem(dto, userDetails.getMemberId());
        return ResponseDto.success(ProblemResponseDto.builder().id(problemId).build());
    }

    // 문제 삭제  2025.05.31. 사용자가 이미 풀었을때 로직 추가 필요
    @DeleteMapping("/delete/{id}")
    public ResponseDto<String> deleteProblem(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails) {
        problemService.deleteProblem(id, userDetails.getMemberId());
        return ResponseDto.success("문제가 성공적으로 삭제되었습니다.");
    }
    // 문제 업데이트 todo: 퀀한 추가
    @PostMapping("/update/{id}")
    public ResponseDto<ProblemResponseDto> updateProblem(@RequestBody ProblemRequestDto.Update dto,@AuthenticationPrincipal MemberUserDetails userDetails) {
        Long problemId = problemService.updateProblem(dto, userDetails.getMemberId());
        return ResponseDto.success(ProblemResponseDto.builder().id(problemId).build());
    }
    // 문제 상세정보
    @GetMapping("/detail/{problemId}")
    public ResponseDto<ProblemResponseDto.Detail> getProblemDetail(@PathVariable Long problemId, @AuthenticationPrincipal MemberUserDetails userDetails) {
        ProblemResponseDto.Detail problemDetail = problemService.getProblemDetail(problemId);
        return ResponseDto.success(problemDetail);
    }

    // 문제 테스트 정보 ( test = testCase = true 만 조회하기 위함 )
    @GetMapping("/detail/{problemId}/test-case")
    public ResponseDto<List<ProblemTestCaseResponseDto.Detail>> getProblemTestDetail(@RequestParam(required = false) Boolean test,
                                                                                     @PathVariable Long problemId,
                                                                                     @AuthenticationPrincipal MemberUserDetails userDetails) {
        List<ProblemTestCaseResponseDto.Detail> testCaseList = problemService.getProblemTestCaseListByProblemId(test, problemId);
        return ResponseDto.success(testCaseList);
    }


    // 문제 답변 리스트
    @GetMapping("/detail/{id}/solved")
    public ResponseDto<String> getProblemSolvedList(@AuthenticationPrincipal MemberUserDetails userDetails) {
        return ResponseDto.success("ok");
    }
}