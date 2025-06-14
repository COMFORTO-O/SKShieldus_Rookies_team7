package com.example.shieldus.controller.dto;

import com.example.shieldus.entity.member.MemberSubmitProblem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SubmissionDto {
    private Long submitProblemId;
    private Long problemId;
    private String problemTitle;
    private Boolean pass;
    private LocalDateTime createdAt;     // createdAt 기준
    private LocalDateTime updatedAt;   // updatedAt 기준
    private LocalDateTime completedAt;     // 임시저장된 코드 중 최근 사용 언어 (있다면)


    public SubmissionDto(Long submitProblemId,
                         Long problemId,
                         String problemTitle,
                         Boolean pass,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt,
                         LocalDateTime completedAt){
        this.submitProblemId = submitProblemId;
        this.problemId = problemId;
        this.problemTitle = problemTitle;
        this.pass = pass;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    public static SubmissionDto from(MemberSubmitProblem entity) {
        return SubmissionDto.builder()
                .submitProblemId(entity.getId())
                .problemId(entity.getProblem().getId())
                .problemTitle(entity.getProblem().getTitle())
                .pass(entity.getPass())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .completedAt(entity.getCompletedAt())
                .build();
    }
}