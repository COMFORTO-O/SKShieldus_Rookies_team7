package com.example.shieldus.controller.dto;


import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProblemRequestDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create{
        private String title;
        private String detail;
        private ProblemCategoryEnum category;
        private Integer level;
        private List<ProblemTestCaseRequestDto.Create> testCase;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update{
        private Long problemId;
        private String title;
        private String detail;
        private ProblemCategoryEnum category;
        private Integer level;
        private List<ProblemTestCaseRequestDto.Update> testCase;
    }
}
