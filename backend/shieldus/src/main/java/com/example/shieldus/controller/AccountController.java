package com.example.shieldus.controller;


import com.example.shieldus.controller.dto.AccountRequest;
import com.example.shieldus.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final MemberService memberService;
    @PostMapping("/register")
    public String registerMember(@RequestBody @Valid AccountRequest.Register dto){
        memberService.register(dto);
        return "ok";
    }
}
