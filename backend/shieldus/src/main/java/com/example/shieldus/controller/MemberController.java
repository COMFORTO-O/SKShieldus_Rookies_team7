package com.example.shieldus.controller;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.controller.dto.MyPageResponseDto;
import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.controller.dto.ResponseDto;
import com.example.shieldus.service.member.MemberService;
import com.example.shieldus.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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

    // 사용자 정보 리스트, pagenation
    @GetMapping("/list")
    public ResponseDto<String> getMemberList(@AuthenticationPrincipal MemberUserDetails userDetails) {

        return ResponseDto.success("ok");
    }

    // 사용자 삭제 ( soft ), 진짜 삭제가 아닌 컬럼 붙여서 삭제
    @GetMapping("/delete")
    public ResponseDto<String> deleteMember(@AuthenticationPrincipal MemberUserDetails userDetails) {
        memberService.deleteMember(userDetails.getMemberId());
        return ResponseDto.success("ok");
    }

    // 푼 문제 가져오기
    @GetMapping("/problem/solved")
    public ResponseDto<Page<ProblemResponseDto>> getSolvedProblem(Pageable pageable, @AuthenticationPrincipal MemberUserDetails userDetails) {
        Page<ProblemResponseDto> problemList =  problemService.getFilteredProblems(userDetails.getMemberId(), null, null, null, "solved", pageable);
        return ResponseDto.success(problemList);
    }

    // 푼 문제 상세정보
    @GetMapping("/problem/solved/detail/{id}")
    public ResponseDto<String> getSolvedProblemDetail(@AuthenticationPrincipal MemberUserDetails userDetails) {
        return ResponseDto.success("ok");
    }

    // 임시저장 확인
    @GetMapping("/problem/temp/{id}")
    public ResponseDto<String> getProblemTemp(@AuthenticationPrincipal MemberUserDetails userDetails) {
        return ResponseDto.success("ok");
    }

}
