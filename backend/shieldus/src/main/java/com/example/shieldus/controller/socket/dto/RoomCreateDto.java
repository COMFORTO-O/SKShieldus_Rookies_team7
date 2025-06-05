package com.example.shieldus.controller.socket.dto;

import lombok.Data;

@Data
public class RoomCreateDto {
    private Long problemId;
    private String language;
    private String code;
}
