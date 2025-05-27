package com.example.shieldus.controller;

import com.example.shieldus.controller.dto.MyPageResponse;
import com.example.shieldus.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;

    @GetMapping("/mypage")
    // 1. 사용자 ID 추출 (예: "user123" → 123L)
    public MyPageResponse getMyPage(@AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = Long.parseLong(userDetails.getUsername());

        // 사용자 ID 추출
        return memberService.getMyPageInfo(memberId);
    }
}
