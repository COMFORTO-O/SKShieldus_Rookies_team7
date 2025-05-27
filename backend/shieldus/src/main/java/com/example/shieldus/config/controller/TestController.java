package com.example.shieldus.config.controller;


import com.example.shieldus.entity.enumration.MemberRoleEnum;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    @GetMapping("/api/account/login/test/register")
    public String test() {
        Member member = Member.builder()
                .email("test@test.com")
                .phone("1231231231")
                .name("test")
                .role(MemberRoleEnum.ADMIN)
                .password(passwordEncoder.encode("test1234"))
                .memberRank(10).build();
        memberRepository.save(member);
        return "0k";
    }
}
