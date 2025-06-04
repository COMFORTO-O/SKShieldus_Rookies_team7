package com.example.shieldus.controller;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.controller.dto.*;
import com.example.shieldus.controller.dto.member.MemberTempCodeResponseDto;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.service.member.MemberService;
import com.example.shieldus.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member") // 공통 URL prefix 설정
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ProblemService problemService;

    @GetMapping("/info")
    public ResponseDto<MyPageResponseDto> getUserInfo(@AuthenticationPrincipal MemberUserDetails userDetails) {
        // 서비스 계층에서 사용자 ID를 기반으로 마이페이지 데이터를 조회
        MyPageResponseDto myPageData = memberService.getMyPageInfo(userDetails.getMemberId());
        // 조회된 데이터를 성공 응답 포맷(ResponseDto)으로 감싸서 반환
        return ResponseDto.success(myPageData);
    }

    @DeleteMapping("/delete")// 사용자 삭제 ( soft ), 진짜 삭제가 아닌 컬럼 붙여서 삭제
    public ResponseDto<String> deleteMember(@AuthenticationPrincipal MemberUserDetails userDetails) {
        memberService.deleteMember(userDetails.getMemberId());
        return ResponseDto.success("ok");
    }

//    @GetMapping("/problem/solved") // 푼 문제 가져오기
//    public ResponseDto<Page<ProblemResponseDto>> getSolvedProblem(Pageable pageable, @AuthenticationPrincipal MemberUserDetails userDetails) {
//        Page<ProblemResponseDto> submitProblemList = memberService.getMemberSubmitProblems(userDetails.getMemberId(), pageable);
//        return ResponseDto.success(submitProblemList);
//    }

    @GetMapping("/problem/solved/detail/{submitProblemId}") // 푼 문제 상세정보 / id = member_submit_problem_id;
    public ResponseDto<ProblemResponseDto.SolvedProblem> getSolvedProblemDetail(
            @PathVariable Long submitProblemId,
            @AuthenticationPrincipal MemberUserDetails userDetails) {
        ProblemResponseDto.SolvedProblem solvedProblem = memberService.getSolvedProblem(submitProblemId, userDetails.getMemberId());
        return ResponseDto.success(solvedProblem);
    }

    // 임시저장 확인
    @GetMapping("/problem/temp/{id}")
    public ResponseDto<MemberTempCodeResponseDto> getProblemTemp( @PathVariable Long problemId, @AuthenticationPrincipal MemberUserDetails userDetails) {
        MemberTempCodeResponseDto tempCodeResponseDto = memberService.getMemberTempCode(userDetails.getMemberId(),problemId);
        return ResponseDto.success(tempCodeResponseDto);
    }

    /*
     * Admin 기능
     * */



}
