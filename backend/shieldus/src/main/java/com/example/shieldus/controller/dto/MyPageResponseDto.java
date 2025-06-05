package com.example.shieldus.controller.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MyPageResponseDto {
    private String name;
    private String email;
    private Page<SubmissionDto> submissions;
}
