package com.example.shieldus.entity.socket;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.controller.socket.dto.OwnerDto;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.socket.enumration.RoomRole;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Room {
    private String id;
    private String title;
    private Long problemId;
    private OwnerDto owner;
    private Map<String, RoomRole> memberRoles = new ConcurrentHashMap<>();

    public Room(String id, String name) {
        this.id = id;
        this.title = name;
    }

    // Getters & Setters 생략 가능 (Lombok 써도 OK)
}