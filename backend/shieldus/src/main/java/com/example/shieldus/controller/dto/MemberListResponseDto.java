package com.example.shieldus.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberListResponseDto {
    private Long memberId;
    private String email;
    private String name;
    private String role;
    private boolean isDeleted;
    // 기타 필요한 필드들...
}
