package com.example.shieldus.controller.dto;

import com.example.shieldus.entity.member.enumration.MemberRoleEnum;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private MemberRoleEnum role;
    private boolean isDeleted;
    // 기타 필요한 필드들...

    public MemberResponseDto(Long id, String email, String name, String phone, MemberRoleEnum role, boolean isDeleted) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.isDeleted = isDeleted;
    }
}
