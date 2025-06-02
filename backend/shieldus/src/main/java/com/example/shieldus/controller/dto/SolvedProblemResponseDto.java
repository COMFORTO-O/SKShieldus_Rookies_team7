package com.example.shieldus.controller.dto;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SolvedProblemResponseDto {
    private Long problemId;
    private String title;
    private String detail;
    private ProblemCategoryEnum category;
    private Integer level;
    private boolean pass;
    private LocalDateTime completeDate;
}
