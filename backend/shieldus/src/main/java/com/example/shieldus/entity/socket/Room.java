package com.example.shieldus.entity.socket;

import com.example.shieldus.config.security.service.MemberUserDetails;
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
    private MemberUserDetails owner;
    private Map<String, RoomRole> memberRoles = new ConcurrentHashMap<>();

    public Room(String id, String name, Long problemId) {
        this.id = id;
        this.title = name;
        this.problemId = problemId;
    }

    // Getters & Setters 생략 가능 (Lombok 써도 OK)
}