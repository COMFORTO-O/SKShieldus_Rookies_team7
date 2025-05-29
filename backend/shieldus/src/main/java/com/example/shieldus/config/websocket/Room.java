package com.example.shieldus.config.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Room {
    private String roomId;
    private String name;

}