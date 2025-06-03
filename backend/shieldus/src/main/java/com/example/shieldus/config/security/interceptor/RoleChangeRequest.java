package com.example.shieldus.config.security.interceptor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleChangeRequest {
    private String targetUsername;
    private RoomRole newRole;
}