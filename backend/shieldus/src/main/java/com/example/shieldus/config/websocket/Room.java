package com.example.shieldus.config.websocket;

import com.example.shieldus.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Room {
    private String roomId;
    private String name;
    private String owner;
    //private Map<Member,RoomRole> memberRoles;
}