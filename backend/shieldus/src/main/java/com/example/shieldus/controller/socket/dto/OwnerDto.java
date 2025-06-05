package com.example.shieldus.controller.socket.dto;

import com.example.shieldus.config.security.service.MemberUserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@Builder
@AllArgsConstructor
@Data
public class OwnerDto {
    private Long id;
    private String username;
    private String email;
    private String name;


    public static OwnerDto from(MemberUserDetails userDetails) {
        return OwnerDto.builder()
                .id(userDetails.getMemberId())
                .username(userDetails.getUsername())
                .email(userDetails.getUsername())
                .name(userDetails.getName())
                .build();
    }
}
