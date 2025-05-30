package com.example.shieldus.controller.dto;

import lombok.Getter;

@Getter
public class CompileRequestDto {
    private Long problemId;
    private String code;
    private String language;
}