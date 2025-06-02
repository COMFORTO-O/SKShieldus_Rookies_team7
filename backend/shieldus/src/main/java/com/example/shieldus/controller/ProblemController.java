package com.example.shieldus.controller;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.controller.dto.*;
import com.example.shieldus.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    /**
     * 1) 문제 목록 조회 (필터 + 페이징)
     */
    @GetMapping
    public ResponseDto<Page<ProblemResponseDto>> getProblems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        Long memberId = (userDetails != null) ? userDetails.getMemberId() : null;
        Page<ProblemResponseDto> page = problemService.getFilteredProblems(
                memberId, category, level, title, status, pageable
        );
        return ResponseDto.success(page);
    }

    /**
     * 2) 문제 상세 조회 (테스트케이스 포함, 로그인 없이 가능)
     */
    @GetMapping("/detail/{id}")
    public ResponseDto<ProblemDetailDto> getProblemDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        Long memberId = (userDetails != null) ? userDetails.getMemberId() : null;
        ProblemDetailDto dto = problemService.getProblemDetail(memberId, id);
        return ResponseDto.success(dto);
    }

    /**
     * 3) 문제 생성 (작성자 인증 필요)
     */
    @PostMapping("/create")
    public ResponseDto<CreateProblemResponseDto> createProblem(
            @RequestBody CreateProblemRequestDto dto,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        CreateProblemResponseDto result = problemService.createProblem(memberId, dto);
        return ResponseDto.success(result);
    }

    /**
     * 4) 문제 수정 (작성자 인증 필요)
     */
    @PutMapping("/update/{id}")
    public ResponseDto<UpdateProblemResponseDto> updateProblem(
            @PathVariable Long id,
            @RequestBody UpdateProblemRequestDto dto,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        if (!dto.getProblemId().equals(id)) {
            return ResponseDto.error(400, "INVALID_PATH_VARIABLE", "PathVariable ID와 RequestBody.problemId가 불일치합니다.");
        }

        Long memberId = userDetails.getMemberId();
        UpdateProblemResponseDto result = problemService.updateProblem(memberId, dto);
        return ResponseDto.success(result);
    }

    /**
     * 5) 문제 삭제 (작성자 인증 필요)
     */
    @DeleteMapping("/delete/{id}")
    public ResponseDto<Void> deleteProblem(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        problemService.deleteProblem(memberId, id);
        return ResponseDto.success(null);
    }

    /**
     * 6) 문제 해결자 목록 조회 (로그인 불필요)
     */
    @GetMapping("/solved-list/{id}")
    public ResponseDto<List<String>> getSolvedList(@PathVariable Long id) {
        List<String> solvedList = problemService.getProblemSolvedList(id);
        return ResponseDto.success(solvedList);
    }

    /**
     * 7) 테스트케이스만 별도 조회 (로그인 불필요)
     */
    @GetMapping("/test-cases/{id}")
    public ResponseDto<List<ProblemDetailDto.TestCaseInfoDto>> getTestCasesOfProblem(@PathVariable Long id) {
        List<ProblemDetailDto.TestCaseInfoDto> cases = problemService.getTestCasesOfProblem(id);
        return ResponseDto.success(cases);
    }
}