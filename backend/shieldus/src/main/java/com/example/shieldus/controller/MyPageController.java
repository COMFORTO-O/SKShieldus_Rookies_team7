package com.example.shieldus.controller;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.controller.dto.MyPageResponseDto;
import com.example.shieldus.controller.dto.ResponseDto;
import com.example.shieldus.repository.member.MemberSubmitProblemRepository;
import com.example.shieldus.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage") // 공통 URL prefix 설정
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final MemberSubmitProblemRepository memberSubmitProblemRepository;
    @GetMapping("/mypage")
    public ResponseDto<MyPageResponseDto> getMyPage(
            @AuthenticationPrincipal MemberUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        MyPageResponseDto myPageData = memberService.getMyPageInfo(userDetails.getMemberId(), page, size);
        return ResponseDto.success(myPageData);
    }
}
