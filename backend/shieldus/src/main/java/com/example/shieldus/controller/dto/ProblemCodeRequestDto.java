package com.example.shieldus.controller.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProblemCodeRequestDto {
    private Long id;
    private String code;
    private String description;

    public void setId(Long id) {
        this.id = id;
    }
}
