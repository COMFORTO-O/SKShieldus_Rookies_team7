package com.example.shieldus.config.security.interceptor;

import com.example.shieldus.entity.member.Member;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Room {
    private String id;
    private String title;
    private Member owner;
    private Map<String, RoomRole> memberRoles = new ConcurrentHashMap<>();
    public Room(String id, String name) {
        this.id = id;
        this.title = name;
    }

    // Getters & Setters 생략 가능 (Lombok 써도 OK)
}