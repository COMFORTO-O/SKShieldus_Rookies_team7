package com.example.shieldus.controller;

import com.example.shieldus.controller.dto.MyPageResponse;
import com.example.shieldus.controller.dto.ResponseDto;
import com.example.shieldus.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage") // 공통 URL prefix 설정
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;

    @GetMapping("/mypage")
    public ResponseDto<MyPageResponse> getMyPage(@AuthenticationPrincipal UserDetails userDetails) {
        // Spring Security를 통해 인증된 사용자의 ID (문자열 형태)를 가져옴
        Long memberId = Long.parseLong(userDetails.getUsername());

        // 서비스 계층에서 사용자 ID를 기반으로 마이페이지 데이터를 조회
        MyPageResponse myPageData = memberService.getMyPageInfo(memberId);

        // 조회된 데이터를 성공 응답 포맷(ResponseDto)으로 감싸서 반환
        return ResponseDto.success(myPageData);
    }
}
