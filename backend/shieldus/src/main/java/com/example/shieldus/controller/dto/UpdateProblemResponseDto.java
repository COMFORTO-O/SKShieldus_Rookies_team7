// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 변경 요약
// - 새로 추가된 DTO: 문제 수정 후 ID만 반환
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

package com.example.shieldus.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProblemResponseDto {
    private Long id;  // 수정된 문제 ID (기존 ID 그대로)
}