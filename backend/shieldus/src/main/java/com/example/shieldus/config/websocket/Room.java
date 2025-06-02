package com.example.shieldus.config.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
public class Room {
    private String roomId;
    private String name;
    private String owner;
    private Map<String, RoomRole> memberRoles = new ConcurrentHashMap<>();

    public Room(String roomId, String name, String owner) {
        this.roomId = roomId;
        this.name = name;
        this.owner = owner;
        this.memberRoles = new ConcurrentHashMap<>();
        this.memberRoles.put(owner, RoomRole.CHAT_AND_EDIT);
    }
}