// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 변경 요약
// - 새로 추가된 DTO: 문제 생성 후 ID만 반환
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

package com.example.shieldus.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateProblemResponseDto {
    private Long id;  // 생성된 문제 ID
}