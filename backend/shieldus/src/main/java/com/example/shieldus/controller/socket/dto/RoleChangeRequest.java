package com.example.shieldus.controller.socket.dto;

import com.example.shieldus.entity.socket.enumration.RoomRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleChangeRequest {
    private String targetUsername;
    private RoomRole newRole;
}