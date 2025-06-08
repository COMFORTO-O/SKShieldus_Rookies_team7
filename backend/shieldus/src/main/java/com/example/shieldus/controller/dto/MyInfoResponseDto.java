package com.example.shieldus.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class MyInfoResponseDto {
    private String name;
    private String email;
    private Integer memberRank;

}
