package com.example.shieldus.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateRequestDto {
    private String email;
    private String name;
    private String role;
    // 비밀번호 변경이 필요하면 추가
    private String newPassword;
    // 기타 수정 가능한 필드들...
}
