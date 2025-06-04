package com.example.shieldus.controller.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class TempProblemResponseDto {
    private Long id;
    private String title;
    private LocalDateTime savedAt;
}
