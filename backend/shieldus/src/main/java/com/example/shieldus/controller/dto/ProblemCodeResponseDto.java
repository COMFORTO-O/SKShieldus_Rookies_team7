package com.example.shieldus.controller.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemCodeResponseDto {
    private Long id;
    private String code;
    private String description;

}
