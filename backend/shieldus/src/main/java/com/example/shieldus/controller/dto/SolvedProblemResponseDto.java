package com.example.shieldus.controller.dto;

import com.example.shieldus.entity.member.MemberSubmitProblem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SolvedProblemResponseDto {
    private Long submitId;           // 제출 기록 ID
    private String memberName;       // 풀이자 이름
    private LocalDateTime solvedAt;  // 풀이 시간
    private Boolean isCorrect;       // 정답 여부

    // 엔티티 → DTO 변환 메서드
    public static SolvedProblemResponseDto fromEntity(MemberSubmitProblem submitProblem) {
        return SolvedProblemResponseDto.builder()
                .submitId(submitProblem.getId())
                .memberName(submitProblem.getMember().getName())
                .solvedAt(submitProblem.getCreatedAt())
                .isCorrect(submitProblem.getIsCorrect())
                .build();
    }
}

