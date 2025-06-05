package com.example.shieldus.controller.dto;

import com.example.shieldus.entity.member.MemberTempCode;
import com.example.shieldus.entity.member.enumration.MemberTempCodeStatusEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberTempCodeDto {
    private Long id;
    private String code;
    private String language;
    private LocalDateTime submitDate;
    private MemberTempCodeStatusEnum status;
    public static MemberTempCodeDto from(MemberTempCode entity) {
        return MemberTempCodeDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .status(entity.getStatus())
                .language(entity.getLangauge())
                .submitDate(entity.getSubmitDate())
                .build();
    }
}
