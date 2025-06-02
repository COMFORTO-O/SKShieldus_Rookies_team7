package com.example.shieldus.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProblemResponseDto {
    private Long id;  // 수정된 문제 ID (기존 ID 그대로)
}