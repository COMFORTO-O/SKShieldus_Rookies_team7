package com.example.shieldus.controller.dto;

import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProblemResponseDto {
    private Long id;
    private String title;
    private String detail;
    private ProblemCategoryEnum category;
    private Integer level;
    private String memberName;
}
