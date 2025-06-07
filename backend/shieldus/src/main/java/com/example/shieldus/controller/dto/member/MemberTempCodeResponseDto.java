package com.example.shieldus.controller.dto.member;


import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.member.MemberTempCode;
import com.example.shieldus.entity.member.enumration.MemberTempCodeStatusEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MemberTempCodeResponseDto {

    private Long id;
    private MemberTempCodeStatusEnum status;
    private String code;
    private LocalDateTime submitDate;

    public static MemberTempCodeResponseDto fromEntity(MemberTempCode memberTempCode) {
        return MemberTempCodeResponseDto.builder()
                .id(memberTempCode.getId())
                .code(memberTempCode.getCode())
                .submitDate(memberTempCode.getSubmitDate())
                .status(memberTempCode.getStatus())
                .build();
    }
}
