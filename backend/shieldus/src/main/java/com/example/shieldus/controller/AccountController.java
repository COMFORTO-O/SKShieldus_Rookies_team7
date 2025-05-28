package com.example.shieldus.controller;

import com.example.shieldus.controller.dto.AccountRequest;
import com.example.shieldus.controller.dto.ResponseDto;
import com.example.shieldus.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account") // 공통 URL prefix 설정
public class AccountController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseDto<String> registerMember(@RequestBody @Valid AccountRequest.Register dto){
        // 회원가입 요청을 서비스 계층에 위임
        memberService.register(dto);

        // 성공 메시지를 통일된 응답 형식으로 반환
        return ResponseDto.success("ok");
    }
}
