// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 변경 요약 (S7-32 → S7-33)
// 1) GET /api/problem:
//    - userDetails가 null일 경우에도 호출 가능하도록 memberId=null 처리 추가
//    - @PageableDefault(sort="id,desc") 로 기본 정렬 순서 변경
// 2) POST /api/problem/create:
//    - DTO 클래스 변경: ProblemRequestDto.Create → CreateProblemRequestDto
//    - 응답 DTO 변경: ProblemResponseDto → CreateProblemResponseDto
//    - service 시그니처: (dto, memberId) → (memberId, dto)
// 3) PUT /api/problem/update/{id}:
//    - @PutMapping 으로 변경 (기존 @PostMapping → @PutMapping)
//    - 경로변수(id)와 DTO의 problemId 일치 여부 검증 추가
//    - DTO: ProblemRequestDto.Update → UpdateProblemRequestDto
//    - 응답 DTO: ProblemResponseDto → UpdateProblemResponseDto
// 4) DELETE /api/problem/delete/{id}:
//    - service 호출 순서 변경 (memberId, problemId)
//    - 반환 타입 Void로 통일
// 5) GET /api/problem/detail/{id}:
//    - DTO 변경: ProblemResponseDto.Detail → ProblemDetailDto
//    - memberId null 허용 로직 추가
// 6) GET /api/problem/detail/{id}/test-case:
//    - test 파라미터 제거 (무조건 모든 TestCase 조회)
//    - DTO 변경: ProblemTestCaseResponseDto.Detail → ProblemDetailDto.TestCaseInfoDto
// 7) GET /api/problem/detail/{id}/solved:
//    - 구현 추가: service.getProblemSolvedList(id) 호출
//    - DTO 변경: String → ProblemDetailDto.SolutionInfoDto
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

package com.example.shieldus.controller;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.controller.dto.*;
import com.example.shieldus.entity.problem.ProblemCode;
import com.example.shieldus.entity.problem.enumration.ProblemLanguageEnum;
import com.example.shieldus.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    // language 조회
    @GetMapping("/languages")
    public ResponseDto<List<ProblemResponseDto.ProblemLanguageDto>> getProblemLanguage() {
        List<ProblemResponseDto.ProblemLanguageDto> dto = ProblemLanguageEnum.getAllLanguages().stream().map(ProblemResponseDto.ProblemLanguageDto::fromEnum).toList();
        return ResponseDto.success(dto);
    }

    /**
     * 1) 문제 목록 조회 (필터링 + 페이징)
     *    • 로그인 없이도 호출 가능
     *    • Optional: category, level, title, status (solved/unsolved)
     *    • Pageable: page, size, sort 등
     *
     * 예시:
     * GET /api/problem
     * GET /api/problem?page=0&size=5&sort=id,desc
     * GET /api/problem?category=JAVA&level=2&status=unsolved&title=정렬&page=0
     */
    @GetMapping
    public ResponseDto<Page<ProblemResponseDto>> getProblems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean solved,
            @PageableDefault(size = 10, sort = "id,desc") Pageable pageable,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        // 변경: userDetails가 null인 경우 memberId도 null로 넘겨서 “solved/unsolved” 필터링 시 항상 false 처리
        Long memberId = (userDetails != null) ? userDetails.getMemberId() : null;
        Page<ProblemResponseDto> page = problemService.getFilteredProblems(
                memberId, category, level, title, solved, pageable
        );
        return ResponseDto.success(page);
    }

    /**
     * 2) 문제 생성 (인증 필요)
     *    • POST /api/problem/create
     *    • RequestBody: CreateProblemRequestDto(JSON)
     *    • Response: CreateProblemResponseDto { id: 생성된 문제 ID }
     */
    @PreAuthorize("hasPermission(null, 'PROBLEM_CREATE')")
    @PostMapping("/create")
    public ResponseDto<CreateProblemResponseDto> createProblem(
            @RequestBody CreateProblemRequestDto dto,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        CreateProblemResponseDto result = problemService.createProblem(userDetails.getMemberId(), dto);
        return ResponseDto.success(result);
    }

    /**
     * 3) 문제 수정 (인증 + 작성자만 가능)
     *    • PUT /api/problem/update/{id}
     *    • PathVariable {id} == requestDto.problemId 여야 함 (검증)
     *    • RequestBody: UpdateProblemRequestDto(JSON)
     *    • Response: UpdateProblemResponseDto { id: 수정된 문제 ID }
     */
    @PreAuthorize("hasPermission(null, 'PROBLEM_UPDATE')")
    @PutMapping("/update/{id}")
    public ResponseDto<UpdateProblemResponseDto> updateProblem(
            @PathVariable Long id,
            @RequestBody UpdateProblemRequestDto dto,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        // 변경: 경로변수와 DTO 내부 problemId 일치 여부 검증
        if (!dto.getProblemId().equals(id)) {
            return ResponseDto.error(
                    400,
                    "INVALID_PATH_VARIABLE",
                    "PathVariable ID와 RequestBody.problemId가 불일치합니다."
            );
        }
        UpdateProblemResponseDto result = problemService.updateProblem(dto);
        return ResponseDto.success(result);
    }

    /**
     * 4) 문제 삭제 (인증 + 작성자만 가능)
     *    • DELETE /api/problem/delete/{id}
     */
    @PreAuthorize("hasPermission(null, 'PROBLEM_DELETE')")
    @DeleteMapping("/delete/{id}")
    public ResponseDto<Void> deleteProblem(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        problemService.deleteProblem(userDetails.getMemberId(), id);
        return ResponseDto.success(null);
    }

    /**
     * 5) 문제 상세 조회 (테스트케이스 포함 + solved 여부 포함)
     *    • 로그인 없이도 호출 가능
     *    • GET /api/problem/detail/{id}
     */
    @GetMapping("/detail/{id}")
    public ResponseDto<ProblemDetailDto> getProblemDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        // 변경: userDetails null 체크하여 memberId 넘김
        Long memberId = (userDetails != null) ? userDetails.getMemberId() : null;
        ProblemDetailDto dto = problemService.getProblemDetail(memberId, id);
        return ResponseDto.success(dto);
    }

    /**
     * 6) 테스트케이스 목록 조회 (인증 없이 가능)
     *    • GET /api/problem/detail/{id}/test-case
     */
    @GetMapping("/detail/{id}/test-case")
    public ResponseDto<List<ProblemDetailDto.TestCaseInfoDto>> getProblemTestCases(@PathVariable Long id) {
        List<ProblemDetailDto.TestCaseInfoDto> list = problemService.getTestCasesOfProblem(id);
        return ResponseDto.success(list);
    }

    /**
     * 7) 해당 문제에 대한 풀이 리스트 조회 (인증 없이도 가능)
     *    • GET /api/problem/detail/{id}/solved
     */
    @GetMapping("/detail/{id}/solved")
    public ResponseDto<List<ProblemDetailDto.SolutionInfoDto>> getProblemSolvedList(
            @PathVariable Long id
    ) {
        List<ProblemDetailDto.SolutionInfoDto> solvedEntries = problemService.getProblemSolvedList(id);
        return ResponseDto.success(solvedEntries);
    }


    // 카테고리 목록 조회
    @GetMapping("/category")
    public ResponseDto<List<ProblemCode>> getCategory(){
        return ResponseDto.success(problemService.getProblemCodes());
    }

    // 카테고리 목록 만들기
    @PreAuthorize("hasPermission(null, 'PROBLEM_CREATE')")
    @PostMapping("/category/create")
    public ResponseDto<ProblemCodeResponseDto> createCategory(
            @RequestBody ProblemCodeRequestDto dto,
            @AuthenticationPrincipal MemberUserDetails userDetails){
        return ResponseDto.success(problemService.createProblemCode(dto));
    }
    // 카테고리 목록 업데이트
    @PreAuthorize("hasPermission(null, 'PROBLEM_UPDATE')")
    @PutMapping("/category/update/{id}")
    public ResponseDto<ProblemCodeResponseDto> updateCategory(
            @PathVariable Long id,
            @RequestBody ProblemCodeRequestDto dto){
        dto.setId(id);
        return ResponseDto.success(problemService.updateProblemCode(dto));
    }

}