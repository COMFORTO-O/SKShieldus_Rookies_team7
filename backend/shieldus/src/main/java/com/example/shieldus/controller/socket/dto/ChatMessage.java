package com.example.shieldus.controller.socket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private String sender;
    private String content;
    private String type; // 예: CHAT, PRIVATE
}