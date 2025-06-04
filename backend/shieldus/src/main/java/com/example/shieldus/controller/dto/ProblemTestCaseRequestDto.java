package com.example.shieldus.controller.dto;


import lombok.*;

@Getter
public class ProblemTestCaseRequestDto {


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create{
        private String input;
        private String output;
    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update{
        private Long testCaseId;
        private String input;
        private String output;
    }
}
